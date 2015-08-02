package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.plugin.buck.build.BuckCommand;
import com.intellij.plugin.buck.build.BuckKillCommandHandler;

/**
 * Run buck kill command
 * It will force terminate all running buck commands and shut down the buck local http server
 */
public class BuckKillAction extends DumbAwareAction {

  public static final String ACTION_TITLE = "Run buck kill";
  public static final String ACTION_DESCRIPTION = "Run buck kill command";

  public BuckKillAction() {
    super(ACTION_TITLE, ACTION_DESCRIPTION, AllIcons.Actions.Suspend);
  }

  @Override
  public void update(AnActionEvent e) {
    boolean isBuckProject = BuckBuildManager.getInstance().isBuckProject(e.getProject());
    e.getPresentation().setEnabled(
        isBuckProject &&
        !BuckBuildManager.getInstance().isKilling() &&
        BuckBuildManager.getInstance().isBuilding());
  }

  @Override
  public void actionPerformed(final AnActionEvent e) {
    BuckKillCommandHandler handler = new BuckKillCommandHandler(
        e.getProject(),
        e.getProject().getBaseDir(),
        BuckCommand.KILL);
    BuckBuildManager.getInstance().runBuckCommand(handler, ACTION_TITLE);
  }
}
