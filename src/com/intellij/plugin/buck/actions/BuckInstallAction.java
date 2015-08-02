package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.build.BuckBuildCommandHandler;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.build.BuckCommand;
import com.intellij.plugin.buck.config.BuckSettingsProvider;

/**
 * Run buck install command
 */
public class BuckInstallAction extends DumbAwareAction {

  public static final String ACTION_TITLE = "Run buck install";
  public static final String ACTION_DESCRIPTION = "Run buck install command";

  public BuckInstallAction() {
    super(ACTION_TITLE, ACTION_DESCRIPTION, AllIcons.Actions.Execute);
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
        BuckCommand.INSTALL);
    if (BuckSettingsProvider.getInstance().getState().runAfterInstall) {
      handler.command().addParameter("-r");
    }
    if (BuckSettingsProvider.getInstance().getState().multiInstallMode) {
      handler.command().addParameter("-x");
    }
    if (BuckSettingsProvider.getInstance().getState().uninstallBeforeInstalling) {
      handler.command().addParameter("-u");
    }
    handler.command().addParameter(target);
    BuckBuildManager.getInstance().runBuckCommand(handler, ACTION_TITLE);
  }
}
