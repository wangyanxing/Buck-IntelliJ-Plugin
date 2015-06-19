package com.intellij.plugin.buck.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.command.BuckCommandUtils;
import com.intellij.plugin.buck.storage.BuckSettingsStorage;
import com.intellij.plugin.buck.ui.ChooseProjectDialog;

public class BuckUninstall extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
    }

    @Override
    public void update(final AnActionEvent e) {
        super.update(e);
        boolean hasHistory = !BuckSettingsStorage.HISTORICAL_PROJECT_NAMES.isEmpty();
        e.getPresentation().setEnabled(hasHistory);
        e.getPresentation().setVisible(hasHistory);

        if (hasHistory) {
            e.getPresentation().setText("Buck uninstall " + BuckSettingsStorage.peekHistory());
        }
    }
}
