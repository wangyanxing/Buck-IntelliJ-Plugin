package com.intellij.plugin.buck.toolwindow;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

public class BuckBuildAction extends DumbAwareAction {
    private ToolWindow myToolWindow;

    public BuckBuildAction() {
        super("Run buck build", "Run buck build command", AllIcons.Actions.Download);
        //myToolWindow = toolWindow;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
    }
}
