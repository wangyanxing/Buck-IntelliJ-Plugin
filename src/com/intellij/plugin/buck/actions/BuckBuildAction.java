package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.targets.BuckTargetUtil;
import com.intellij.plugin.buck.utils.BuckBuildManager;

public class BuckBuildAction extends DumbAwareAction {

  public BuckBuildAction() {
    super("Run buck build", "Run buck build command", AllIcons.Actions.Download);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    String target = BuckTargetUtil.getCurrentSavedTarget(e.getProject());
    if (target == null) {
      return;
    }
    BuckBuildManager.getInstance().build(
        BuckBuildManager.Command.BUILD,
        e.getProject(),
        target);
  }
}
