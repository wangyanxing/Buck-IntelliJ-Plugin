package com.intellij.plugin.buck.utils;

import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import com.intellij.util.OpenSourceUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run buck build commands in background thread, then parse the output from stderr and print to
 * IntelliJ's console window.
 * <p/>
 * We can also create a websocket client and collect information from buck's local http server.
 * However, http server of buck is still immaturity, lots of useful information is not available.
 * For example, we can't get compiler error outputs. Therefore the only way for us is parsing the
 * stderr output of the buck process.
 */
public class BuckBuildManager {

  /**
   * Build command types
   */
  public enum Command {
    BUILD,
    INSTALL,
    UNINSTALL,
  }

  private static BuckBuildManager sInstance;
  private static final String BUCK_BUILD_MESSAGE = "Building with buck";
  private static final Map<Command, String[]> sCommands = new HashMap<Command, String[]>();
  private static final String[] IGNORED_OUTPUT_LINES = new String[]{
      "Using buckd.",
      "Using watchman.",
  };
  private static final String[] ERROR_PREFIXES = new String[]{
      "BUILD FAILED:",
      "FAIL",
      "Errors:",
      "No devices found",
      "NameError",
  };
  private static final String[] SUCCESS_PREFIXES = new String[]{
      "OK ",
      "Successfully",
      "[-] BUILDING...FINISHED",
  };

  private static final Pattern JAVA_FILE_PATTERN =
      Pattern.compile("^([\\s\\S]*\\.java):([0-9]+)([\\s\\S]*)$");
  private static final Pattern BUILD_PROGRESS_PATTERN =
      Pattern.compile("^BUILT //[\\s\\S]*\\(([0-9]+)/([0-9]+) JOBS\\)$");

  private ProgressIndicator mProgressIndicator;
  private boolean mIsBuilding = false;

  public static synchronized BuckBuildManager getInstance() {
    if (sInstance == null) {
      sCommands.put(Command.BUILD, new String[]{
          "buck",
          "build",
          "$",
      });
      sCommands.put(Command.INSTALL, new String[]{
          "buck",
          "install",
          "$",
          "--run",
      });
      sCommands.put(Command.UNINSTALL, new String[]{
          "buck",
          "uninstall",
          "$"
      });
      sInstance = new BuckBuildManager();
    }
    return sInstance;
  }

  /**
   * Run build build command in background threads
   * We also get a ProgressIndicator here and use it to set progress later
   */
  public void build(Command buildCommand, final Project project, String target) {
    setBuilding(true);
    FileDocumentManager.getInstance().saveAllDocuments();

    String[] command = sCommands.get(buildCommand).clone();
    for (int i = 0; i < command.length; ++i) {
      if (command[i].equals("$")) {
        command[i] = target;
      }
    }
    final String[] commandForTask = command.clone();

    String headMessage = "Running '";
    for (int i = 0; i < commandForTask.length; ++i) {
      headMessage += commandForTask[i];
      headMessage += i == commandForTask.length - 1 ? "'" : " ";
    }
    BuckToolWindowFactory.cleanConsole();
    BuckToolWindowFactory.outputConsoleMessage(headMessage + "\n",
        ConsoleViewContentType.NORMAL_OUTPUT);

    final Task.Backgroundable task = new Task.Backgroundable(
        project, BUCK_BUILD_MESSAGE, true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        mProgressIndicator = indicator;
        try {
          Runtime rt = Runtime.getRuntime();
          Process process = rt.exec(
              commandForTask,
              null,
              new File(project.getBasePath()));

          BufferedReader stdError =
              new BufferedReader(new InputStreamReader(process.getErrorStream()));

          String s;
          while ((s = stdError.readLine()) != null) {
            parseOutputLine(project, s.trim());
          }
          BuckBuildManager.getInstance().setBuilding(false);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    ProgressManager.getInstance().run(task);
  }

  public synchronized void setProgress(double fraction) {
    if (mProgressIndicator == null) {
      return;
    }
    mProgressIndicator.setFraction(fraction);
    mProgressIndicator.checkCanceled();
  }

  /**
   * Get saved target for this project from settings
   */
  public String getCurrentSavedTarget(Project project) {
    if (BuckSettingsProvider.getInstance().getState().lastAlias == null) {
      return null;
    }
    return BuckSettingsProvider.getInstance().getState().lastAlias.get(project.getBasePath());
  }

  public boolean isBuilding() {
    return mIsBuilding;
  }

  public synchronized void setBuilding(boolean value) {
    mIsBuilding = value;
    BuckToolWindowFactory.updateActionsNow();
  }

  public void showNoTargetMessage() {
    BuckToolWindowFactory.outputConsoleMessage("Please ", ConsoleViewContentType.ERROR_OUTPUT);
    BuckToolWindowFactory.outputConsoleHyperlink(
        "choose a build target!\n",
        new HyperlinkInfo() {
          @Override
          public void navigate(Project project) {
            // Jump to "Choose target" UI
            JComponent frame = WindowManager.getInstance().getIdeFrame(project).getComponent();
            AnAction action = ActionManager.getInstance().getAction("buck.ChooseTarget");
            action.actionPerformed(
                new AnActionEvent(null, DataManager.getInstance().getDataContext(frame),
                    ActionPlaces.UNKNOWN, action.getTemplatePresentation(),
                    ActionManager.getInstance(), 0)
            );
          }
        });
  }

  /**
   * Parse a line of the buck command output for:
   * 1. Calculate the progress
   * 2. Ignore unused lines, for example "Using buckd."
   * 3. Print to console window with different colors
   */
  private void parseOutputLine(Project project, String line) {
    for (String ignored : IGNORED_OUTPUT_LINES) {
      if (line.matches(ignored)) {
        return;
      }
    }

    // Extract the jobs information and calculate the progress
    Matcher matcher = BUILD_PROGRESS_PATTERN.matcher(line);
    if (matcher.matches()) {
      double finishedJob = Double.parseDouble(matcher.group(1));
      double totalJob = Double.parseDouble(matcher.group(2));
      setProgress(finishedJob / totalJob);
      return;
    }

    // Red color
    for (String errorPrefix : ERROR_PREFIXES) {
      if (line.startsWith(errorPrefix)) {
        BuckToolWindowFactory.outputConsoleMessage(
            line + "\n", ConsoleViewContentType.ERROR_OUTPUT);
        return;
      }
    }

    // Green color
    for (String successPrefix : SUCCESS_PREFIXES) {
      if (line.startsWith(successPrefix)) {
        BuckToolWindowFactory.outputConsoleMessage(
            line + "\n", ConsoleViewContentType.USER_INPUT);
        return;
      }
    }

    // Test if it is a java compile error message with a java file path
    Matcher javaErrorMatcher = JAVA_FILE_PATTERN.matcher(line);
    if (javaErrorMatcher.matches()) {
      String javaFile = javaErrorMatcher.group(1);
      String lineNumber = javaErrorMatcher.group(2);
      String errorMessage = javaErrorMatcher.group(3);

      String relativePath = javaFile.replaceAll(project.getBasePath(), "");
      final VirtualFile virtualFile = pathToVirtualFile(project, relativePath);
      if (virtualFile == null) {
        BuckToolWindowFactory.outputConsoleMessage(
            line + "\n", ConsoleViewContentType.ERROR_OUTPUT);
      } else {
        BuckToolWindowFactory.outputConsoleHyperlink(relativePath + ":" + lineNumber,
            new HyperlinkInfo() {
              @Override
              public void navigate(Project project) {
                OpenSourceUtil.navigate(true, new OpenFileDescriptor(project, virtualFile));
              }
            });
        BuckToolWindowFactory.outputConsoleMessage(
            errorMessage + "\n", ConsoleViewContentType.ERROR_OUTPUT);
      }
    } else {
      BuckToolWindowFactory.outputConsoleMessage(
          line + "\n", ConsoleViewContentType.NORMAL_OUTPUT);
    }
  }

  private VirtualFile pathToVirtualFile(Project project, String relativePath) {
    VirtualFile projectPath = project.getBaseDir();
    return projectPath.findFileByRelativePath(relativePath);
  }
}
