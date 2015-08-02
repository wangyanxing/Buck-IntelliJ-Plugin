package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.build.BuckBuildCommandHandler;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.plugin.buck.build.BuckCommand;

/**
 * Run buck uninstall command
 */
public class BuckUninstallAction extends DumbAwareAction {

  public static final String ACTION_TITLE = "Run buck uninstall";
  public static final String ACTION_DESCRIPTION = "Run buck uninstall command";

  public BuckUninstallAction() {
    super(ACTION_TITLE, ACTION_DESCRIPTION, AllIcons.Actions.Delete);
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

    BuckBuildCommandHandler handler = new BuckBuildCommandHandler(
        e.getProject(),
        e.getProject().getBaseDir(),
        BuckCommand.UNINSTALL);
    handler.command().addParameter(target);
    BuckBuildManager.getInstance().runBuckCommand(handler, ACTION_TITLE);
  }
}
