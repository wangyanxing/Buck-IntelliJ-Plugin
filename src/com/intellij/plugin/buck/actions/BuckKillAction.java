package com.intellij.plugin.buck.actions;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class BuckKillAction extends DumbAwareAction {

  private boolean mKilling = false;

  public BuckKillAction() {
    super("Stop building", "Stop buck building commands", AllIcons.Actions.Suspend);
  }

  @Override
  public void update(AnActionEvent e) {
    e.getPresentation().setEnabled(!mKilling && BuckBuildManager.getInstance().isBuilding());
  }

  @Override
  public void actionPerformed(final AnActionEvent e) {
    final File basePath = new File(e.getProject().getBasePath());
    final String[] commandForTask = {"buck", "kill"};
    mKilling = true;
    final Task task = new Task.Backgroundable(
        e.getProject(), "Killing buck building processes", true) {
      @Override
      public void run(@NotNull final ProgressIndicator indicator) {
        try {
          Runtime rt = Runtime.getRuntime();
          rt.exec(commandForTask, null, basePath);
          BuckBuildManager.getInstance().setBuilding(false);
          BuckToolWindowFactory.outputConsoleMessage("Build aborted\n", ConsoleViewContentType.ERROR_OUTPUT);
          mKilling = false;
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    ProgressManager.getInstance().run(task);
  }
}
