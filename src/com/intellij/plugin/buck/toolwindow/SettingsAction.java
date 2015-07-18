package com.intellij.plugin.buck.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

public class SettingsAction extends DumbAwareAction {
  private ToolWindow myToolWindow;

  public SettingsAction(@NotNull ToolWindow toolWindow) {
    super("Settings", "Edit Buck settings for current project", AllIcons.General.ProjectSettings);
    myToolWindow = toolWindow;
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
  }
}
