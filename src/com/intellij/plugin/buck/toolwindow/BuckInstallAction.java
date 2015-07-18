package com.intellij.plugin.buck.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

public class BuckInstallAction extends DumbAwareAction {

    public BuckInstallAction() {
        super("Run buck install", "Run buck install command", AllIcons.Actions.Execute);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }
}
