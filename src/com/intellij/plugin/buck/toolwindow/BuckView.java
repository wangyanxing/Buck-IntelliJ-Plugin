package com.intellij.plugin.buck.toolwindow;

import com.intellij.openapi.components.ProjectComponent;
import org.jetbrains.annotations.NotNull;

public class BuckView implements ProjectComponent {
    @Override
    public void projectOpened() {
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "BuckView";
    }
}
