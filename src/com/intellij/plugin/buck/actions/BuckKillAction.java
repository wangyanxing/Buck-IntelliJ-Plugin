package com.intellij.plugin.buck.actions;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import com.intellij.plugin.buck.utils.CommandUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class BuckKillAction extends DumbAwareAction {

  public BuckKillAction() {
    super("Stop building", "Stop buck building commands", AllIcons.Actions.Suspend);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    try {
      Runtime.getRuntime().exec(
          new String[]{"buck", "kill"},
          null,
          //new File("/Users/cjlm/fbandroid-hg"));
          new File(e.getProject().getBasePath()));
    } catch (IOException e1) {
      e1.printStackTrace();
    } finally {
      BuckToolWindowFactory.outputConsoleMessage(
          "Build aborted\n",
          ConsoleViewContentType.ERROR_OUTPUT);
    }
  }

}
