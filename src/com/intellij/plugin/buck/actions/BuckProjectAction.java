package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.build.BuckBuildCommandHandler;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.build.BuckCommand;

/**
 * Run buck kill command
 * It will force terminate all running buck commands and shut down the buck local http server
 */
public class BuckProjectAction extends DumbAwareAction {

    public static final String ACTION_TITLE = "Run buck project";
    public static final String ACTION_DESCRIPTION = "Run buck project command";

    public BuckProjectAction() {
        super(ACTION_TITLE, ACTION_DESCRIPTION, AllIcons.Actions.Compile);
    }

    @Override
    public void update(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            e.getPresentation().setEnabled(!BuckBuildManager.getInstance(project).isBuilding());
        }
    }

    @Override
    public void actionPerformed(final AnActionEvent e) {
        BuckBuildManager buildManager = BuckBuildManager.getInstance(e.getProject());
        String target = buildManager.getCurrentSavedTarget(e.getProject());

        if (target == null) {
            buildManager.showNoTargetMessage(e.getProject());
            return;
        }

        BuckBuildCommandHandler handler = new BuckBuildCommandHandler(
                e.getProject(),
                e.getProject().getBaseDir(),
                BuckCommand.PROJECT);
        handler.command().addParameter(target);
        buildManager.runBuckCommand(handler, ACTION_TITLE);
    }
}
