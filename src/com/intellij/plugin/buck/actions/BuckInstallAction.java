package com.intellij.plugin.buck.actions;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import com.intellij.plugin.buck.utils.BuckBuildManager;

public class BuckInstallAction extends DumbAwareAction {

  public BuckInstallAction() {
    super("Run buck install", "Run buck install command", AllIcons.Actions.Execute);
  }

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(!BuckBuildManager.getInstance().isBuilding());
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    String target = BuckBuildManager.getInstance().getCurrentSavedTarget(e.getProject());
    if (target == null) {
      BuckToolWindowFactory.outputConsoleMessage(
          "Please choose a build target!\n", ConsoleViewContentType.ERROR_OUTPUT);
      return;
    }
    BuckBuildManager.getInstance().build(
        BuckBuildManager.Command.INSTALL,
        e.getProject(),
        target);
  }

}
