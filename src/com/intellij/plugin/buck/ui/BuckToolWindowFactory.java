package com.intellij.plugin.buck.ui;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.plugin.buck.actions.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BuckToolWindowFactory implements ToolWindowFactory, DumbAware {

  @NonNls
  private static final String OUTPUT_WINDOW_CONTENT_ID = "BuckOutputWindowContent";
  private static final String TOOL_WINDOW_ID = "Buck";
  private static ConsoleView sConsoleWindow;

  public static ToolWindow getBuckToolWindow(Project project) {
    return ToolWindowManager.getInstance(project).getToolWindow(TOOL_WINDOW_ID);
  }

  public static void outputConsoleMessage(String message, ConsoleViewContentType type) {
    if (sConsoleWindow != null) {
      sConsoleWindow.print(message, type);
    }
  }

  @Override
  public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
    toolWindow.setAvailable(true, null);
    toolWindow.setToHideOnEmptyContent(true);
    toolWindow.setTitle(TOOL_WINDOW_ID);

    RunnerLayoutUi layoutUi = RunnerLayoutUi.Factory.getInstance(project).create(
        "buck", "buck", "buck", project);
    Content consoleContent = createConsoleContent(layoutUi, project);

    layoutUi.addContent(consoleContent, 0, PlaceInGrid.center, false);
    layoutUi.getOptions().setLeftToolbar(getLeftToolbarActions(project), ActionPlaces.UNKNOWN);

    final ContentManager contentManager = toolWindow.getContentManager();
    Content content = contentManager.getFactory().createContent(layoutUi.getComponent(), "", true);
    contentManager.addContent(content);
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
    DefaultActionGroup group = new DefaultActionGroup();
    group.add(new ChooseTargetAction(project));
    group.addSeparator();
    group.add(new BuckInstallAction());
    group.add(new BuckBuildAction());
    group.add(new BuckKillAction());
    group.add(new BuckUninstallAction());
    return group;
  }
}
