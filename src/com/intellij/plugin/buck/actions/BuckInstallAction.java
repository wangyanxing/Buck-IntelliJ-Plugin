package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;

public class BuckInstallAction extends DumbAwareAction {

  public BuckInstallAction() {
    super("Run buck install", "Run buck install command", AllIcons.Actions.Execute);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
  }
}
