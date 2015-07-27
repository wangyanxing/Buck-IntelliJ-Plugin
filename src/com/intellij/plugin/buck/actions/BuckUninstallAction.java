package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.build.BuckBuildManager;

public class BuckUninstallAction extends DumbAwareAction {
  public BuckUninstallAction() {
    super("Run buck uninstall", "Run buck uninstall command", AllIcons.Actions.Delete);
  }

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(!BuckBuildManager.getInstance().isBuilding());
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    String target = BuckBuildManager.getInstance().getCurrentSavedTarget(e.getProject());
    if (target == null) {
      BuckBuildManager.getInstance().showNoTargetMessage();
      return;
    }
    BuckBuildManager.getInstance().build(
        BuckBuildManager.Command.UNINSTALL,
        e.getProject(),
        target);
  }
}
