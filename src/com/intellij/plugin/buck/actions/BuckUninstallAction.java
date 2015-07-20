package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.utils.CommandUtils;

public class BuckUninstallAction extends DumbAwareAction {
  public BuckUninstallAction() {
    super("Run buck uninstall", "Run buck uninstall command", AllIcons.Actions.Delete);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
//    String[] command = {"buck", "build", "oculus-store"};
//    CommandUtils.runCommandInBackground(
//        e.getProject(),
//        "/Users/cjlm/fbandroid-hg",
//        command);
  }
}
