package com.intellij.plugin.buck.utils;

import com.google.common.io.Files;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class BuckBuildManager {

  public enum Command {
    BUILD,
    INSTALL,
    UNINSTALL,
  }

  private class BuildCommand {
    public Command command;
    public Project project;
    public String target;
  }

  private static BuckBuildManager sInstance;
  private static final String QUERY_PORT_MESSAGE = "Querying buck local service port";
  private static final String BUCK_BUILD_MESSAGE = "Building with buck";
  private static final Map<Command, String[]> sCommands = new HashMap<Command, String[]>();
  private static final String sTempFilePath = Files.createTempDir().getAbsoluteFile() + "/buck";

  private BuckProgressListener mProgressListener;
  private BuildCommand mPostponingCommand;

  public static synchronized BuckBuildManager getInstance() {
    if (sInstance == null) {
      sCommands.put(Command.BUILD, new String[] {
          "buck",
          "build",
          "$",
          "--keep-going",
          "--build-report",
          sTempFilePath
      });
      sCommands.put(Command.INSTALL, new String[] {
          "buck",
          "install",
          "$",
          "--keep-going",
          "--run",
          "--build-report",
          sTempFilePath
      });
      sCommands.put(Command.UNINSTALL, new String[] {
          "buck",
          "uninstall",
          "$"
      });
      sInstance = new BuckBuildManager();
    }
    return sInstance;
  }

  /**
   * Run build build command
   * Firstly we have to check if the websocket client is connected to local buck server.
   */
  public void build(Command command, Project project, String target) {
    assert mPostponingCommand == null;

    mPostponingCommand = new BuildCommand();
    mPostponingCommand.command = command;
    mPostponingCommand.project = project;
    mPostponingCommand.target = target;

    if (listeningServer()) {
      processPostponingCommand();
    } else {
      //queryPortNumber(project, "/Users/cjlm/fbandroid-hg");
      queryPortNumber(project, mPostponingCommand.project.getBasePath());
    }
  }

  public void processPostponingCommand() {
    if (mPostponingCommand == null) {
      return;
    }

    String[] command = sCommands.get(mPostponingCommand.command).clone();
    for(int i = 0; i < command.length; ++i) {
      if (command[i].equals("$")) {
        command[i] = mPostponingCommand.target;
      }
    }

    final String[] commandForTask = command.clone();
    final Task task = new Task.Backgroundable(
        mPostponingCommand.project, BUCK_BUILD_MESSAGE, true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        try {
          Runtime rt = Runtime.getRuntime();
          Process process = rt.exec(
              commandForTask,
              null,
              //new File("/Users/cjlm/fbandroid-hg"));
              new File(mPostponingCommand.project.getBasePath()));

          mPostponingCommand = null;
          BufferedReader stdError =
              new BufferedReader(new InputStreamReader(process.getErrorStream()));

          String s;
          while ((s = stdError.readLine()) != null) {
            System.out.println(s);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    ProgressManager.getInstance().run(task);
  }

  /**
   * Check if the port number is still available
   */
  private boolean listeningServer() {
    return mProgressListener != null && mProgressListener.isOpen();
  }

  /**
   * Get port number of the local buck server
   */
  private void queryPortNumber(Project project, final String projectDir) {
    BuckToolWindowFactory.outputConsoleMessage(
        "Listening to local buck server ",
        ConsoleViewContentType.NORMAL_OUTPUT);

    final Task task = new Task.Backgroundable(project, QUERY_PORT_MESSAGE, true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        String[] command = {"buck", "server", "status", "--http-port"};
        String port = CommandUtils.runBuckCommand(projectDir, command);
        port = port.substring(port.indexOf('=') + 1).trim();

        BuckToolWindowFactory.outputConsoleMessage(
            "with port " + port + "\n",
            ConsoleViewContentType.NORMAL_OUTPUT);

        try {
          String uri = "ws://localhost:" + port + "/ws/build";
          mProgressListener = new BuckProgressListener(new URI(uri));
          mProgressListener.connect();
        } catch (URISyntaxException e) {
          e.printStackTrace();
        }
      }
    };
    ProgressManager.getInstance().run(task);
  }

}
