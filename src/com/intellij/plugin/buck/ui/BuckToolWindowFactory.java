package com.intellij.plugin.buck.ui;

import com.intellij.execution.filters.HyperlinkInfo;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.plugin.buck.actions.ChooseTargetAction;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BuckToolWindowFactory implements ToolWindowFactory, DumbAware {

  @NonNls
  private static final String OUTPUT_WINDOW_CONTENT_ID = "BuckOutputWindowContent";
  private static ConsoleView sConsoleWindow;
  private static RunnerLayoutUi sRunnerLayoutUi;
  public static final String TOOL_WINDOW_ID = "Buck";

  public static void updateBuckToolWindowTitle(Project project) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    String target = BuckBuildManager.getInstance().getCurrentSavedTarget(project);
    if (target != null) {
      toolWindow.setTitle("Target: " + target);
    }
  }

  public static boolean isToolWindowVisible(Project project) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    return toolWindow.isVisible();
  }

  public static synchronized void outputConsoleMessage(
      String message, ConsoleViewContentType type) {
    if (sConsoleWindow != null) {
      sConsoleWindow.print(message, type);
    }
  }

  public static synchronized void outputConsoleHyperlink(String link, HyperlinkInfo linkInfo) {
    if (sConsoleWindow != null) {
      sConsoleWindow.printHyperlink(link, linkInfo);
    }
  }

  public static synchronized void cleanConsole() {
    if (sConsoleWindow != null) {
      sConsoleWindow.clear();
    }
  }

  public static synchronized void updateActionsNow() {
    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        if (sRunnerLayoutUi != null) {
          sRunnerLayoutUi.updateActionsNow();
        }
      }
    });
  }

  @Override
  public void createToolWindowContent(
      @NotNull final Project project, @NotNull ToolWindow toolWindow) {
    toolWindow.setAvailable(true, null);
    toolWindow.setToHideOnEmptyContent(true);

    sRunnerLayoutUi = RunnerLayoutUi.Factory.getInstance(project).create(
        "buck", "buck", "buck", project);
    Content consoleContent = createConsoleContent(sRunnerLayoutUi, project);

    sRunnerLayoutUi.addContent(consoleContent, 0, PlaceInGrid.center, false);
    sRunnerLayoutUi.getOptions().setLeftToolbar(
        getLeftToolbarActions(project), ActionPlaces.UNKNOWN);

    sRunnerLayoutUi.updateActionsNow();

    final ContentManager contentManager = toolWindow.getContentManager();
    Content content = contentManager.getFactory().createContent(
        sRunnerLayoutUi.getComponent(), "", true);
    contentManager.addContent(content);

    updateBuckToolWindowTitle(project);

//    if (!BuckBuildUtil.isBuckProject(project)) {
//      outputConsoleMessage("Not a Buck project!", ConsoleViewContentType.ERROR_OUTPUT);
//    }
  }

  private Content createConsoleContent(RunnerLayoutUi layoutUi, Project project) {
    sConsoleWindow = new ConsoleViewImpl(project, false);
    Content consoleWindowContent = layoutUi.createContent(
        OUTPUT_WINDOW_CONTENT_ID, sConsoleWindow.getComponent(), "Output Logs", null, null);
    consoleWindowContent.setCloseable(false);
    return consoleWindowContent;
  }

  @NotNull
  public ActionGroup getLeftToolbarActions(final Project project) {
    ActionManager actionManager = ActionManager.getInstance();

    ChooseTargetAction chooseTargetAction = (ChooseTargetAction) actionManager.getAction(
        "buck.ChooseTarget");

    DefaultActionGroup group = new DefaultActionGroup();
    group.add(chooseTargetAction.init(project));
    group.addSeparator();
    group.add(actionManager.getAction("buck.Install"));
    group.add(actionManager.getAction("buck.Build"));
    group.add(actionManager.getAction("buck.Kill"));
    group.add(actionManager.getAction("buck.Uninstall"));
    return group;
  }
}
