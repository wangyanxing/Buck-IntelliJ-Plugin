package com.intellij.plugin.buck.toolwindow;

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
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BuckToolWindowFactory implements ToolWindowFactory, DumbAware {
    public static final String TOOL_WINDOW_ID = "Buck";

    @NonNls
    private static final String OUTPUT_WINDOW_CONTENT_ID = "OutputWindowContent";

    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
        RunnerLayoutUi layoutUi = RunnerLayoutUi.Factory.getInstance(project).create(
                "buck", "buck", "buck", project);

        toolWindow.setAvailable(true, null);
        toolWindow.setToHideOnEmptyContent(true);
        toolWindow.setTitle(TOOL_WINDOW_ID);

        Content consoleContent = createAdbLogsContent(layoutUi, project);

        layoutUi.addContent(consoleContent, 0, PlaceInGrid.center, false);
        layoutUi.getOptions().setLeftToolbar(getLeftToolbarActions(), ActionPlaces.UNKNOWN);

        final ContentManager contentManager = toolWindow.getContentManager();
        Content c = contentManager.getFactory().createContent(layoutUi.getComponent(), "Build System", true);
        contentManager.addContent(c);
    }

    private Content createAdbLogsContent(RunnerLayoutUi layoutUi, Project project) {
        final ConsoleView console = new ConsoleViewImpl(project, false);
        Content adbLogsContent = layoutUi.createContent(
                OUTPUT_WINDOW_CONTENT_ID, console.getComponent(), "Output Logs", null, null);
        adbLogsContent.setCloseable(false);
        return adbLogsContent;
    }

    @NotNull
    public ActionGroup getLeftToolbarActions() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new BuckInstallAction());
        group.add(new BuckBuildAction());
        group.add(new BuckUninstallAction());
        return group;
    }
}
