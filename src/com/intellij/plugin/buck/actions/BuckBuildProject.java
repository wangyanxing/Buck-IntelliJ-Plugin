package com.intellij.plugin.buck.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.command.BuckCommandUtils;
import com.intellij.plugin.buck.ui.ChooseProjectDialog;

public class BuckBuildProject extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        new ChooseProjectDialog(project, BuckCommandUtils.COMMAND_TYPE.COMMAND_BUILD).show();
    }
}
