package com.intellij.plugin.buck.toolbar;


import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;

public class BuckToolsActionGroup extends DefaultActionGroup {
  @Override
  public void update(AnActionEvent e) {
    final Project project = e.getData(CommonDataKeys.PROJECT);
    e.getPresentation().setVisible(project != null && !project.isDisposed());
  }
}