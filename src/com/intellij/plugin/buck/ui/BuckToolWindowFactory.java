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

  private static final String OUTPUT_WINDOW_CONTENT_ID = "BuckOutputWindowContent";
  public static final String TOOL_WINDOW_ID = "Buck";

  public static void updateBuckToolWindowTitle(Project project) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    String target = BuckBuildManager.getInstance(project).getCurrentSavedTarget(project);
    if (target != null) {
      toolWindow.setTitle("Target: " + target);
    }
  }

  public static boolean isToolWindowVisible(Project project) {
    ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
    return toolWindow.isVisible();
  }

  public static synchronized void outputConsoleMessage(
      Project project, String message, ConsoleViewContentType type) {
    BuckUIManager.getInstance(project).getConsoleWindow(project).print(message, type);
  }

  public static synchronized void outputConsoleHyperlink(
      Project project, String link, HyperlinkInfo linkInfo) {
    BuckUIManager.getInstance(project).getConsoleWindow(project).printHyperlink(link, linkInfo);
  }

  public static synchronized void cleanConsole(Project project) {
    BuckUIManager.getInstance(project).getConsoleWindow(project).clear();
  }

  public static synchronized void updateActionsNow(final Project project) {

    ApplicationManager.getApplication().invokeLater(new Runnable() {
      @Override
      public void run() {
        BuckUIManager.getInstance(project).getLayoutUi(project).updateActionsNow();
      }
    });
  }

  @Override
  public void createToolWindowContent(
      @NotNull final Project project, @NotNull ToolWindow toolWindow) {
    toolWindow.setAvailable(true, null);
    toolWindow.setToHideOnEmptyContent(true);

    RunnerLayoutUi runnerLayoutUi = BuckUIManager.getInstance(project).getLayoutUi(project);
    Content consoleContent = createConsoleContent(runnerLayoutUi, project);

    runnerLayoutUi.addContent(consoleContent, 0, PlaceInGrid.center, false);
    runnerLayoutUi.getOptions().setLeftToolbar(
        getLeftToolbarActions(project), ActionPlaces.UNKNOWN);

    runnerLayoutUi.updateActionsNow();

    final ContentManager contentManager = toolWindow.getContentManager();
    Content content = contentManager.getFactory().createContent(
        runnerLayoutUi.getComponent(), "", true);
    contentManager.addContent(content);

    updateBuckToolWindowTitle(project);
  }

  private Content createConsoleContent(RunnerLayoutUi layoutUi, Project project) {
    ConsoleView consoleView = BuckUIManager.getInstance(project).getConsoleWindow(project);
    Content consoleWindowContent = layoutUi.createContent(
        OUTPUT_WINDOW_CONTENT_ID, consoleView.getComponent(), "Output Logs", null, null);
    consoleWindowContent.setCloseable(false);
    return consoleWindowContent;
  }

  @NotNull
  public ActionGroup getLeftToolbarActions(final Project project) {
    ActionManager actionManager = ActionManager.getInstance();

    DefaultActionGroup group = new DefaultActionGroup();
    group.add(actionManager.getAction("buck.ChooseTarget"));
    group.addSeparator();
    group.add(actionManager.getAction("buck.Install"));
    group.add(actionManager.getAction("buck.Build"));
    group.add(actionManager.getAction("buck.Kill"));
    group.add(actionManager.getAction("buck.Uninstall"));
    return group;
  }
}
