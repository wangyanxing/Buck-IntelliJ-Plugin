package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.build.BuckBuildCommandHandler;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.plugin.buck.build.BuckCommand;

/**
 * Run buck build command
 */
public class BuckBuildAction extends DumbAwareAction {

  public static final String ACTION_TITLE = "Run buck build";
  public static final String ACTION_DESCRIPTION = "Run buck build command";

  public BuckBuildAction() {
    super(ACTION_TITLE, ACTION_DESCRIPTION, AllIcons.Actions.Download);
  }

  @Override
  public void update(AnActionEvent e) {
    boolean isBuckProject = BuckBuildManager.getInstance().isBuckProject(e.getProject());
    e.getPresentation().setEnabled(isBuckProject && !BuckBuildManager.getInstance().isBuilding());
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
        BuckCommand.BUILD);
    handler.command().addParameter(target);
    BuckBuildManager.getInstance().runBuckCommand(handler, ACTION_TITLE);
  }
}
