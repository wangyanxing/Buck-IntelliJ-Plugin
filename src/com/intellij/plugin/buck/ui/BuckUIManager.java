package com.intellij.plugin.buck.ui;


import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;

public class BuckUIManager {

  private ConsoleView mOutputConsole;
  private RunnerLayoutUi mRunnerLayoutUi;

  public static synchronized BuckUIManager getInstance(Project project) {
    return ServiceManager.getService(project, BuckUIManager.class);
  }

  public ConsoleView getConsoleWindow(Project project) {
    if (mOutputConsole == null) {
      mOutputConsole = new ConsoleViewImpl(project, false);
    }
    return mOutputConsole;
  }

  public RunnerLayoutUi getLayoutUi(Project project) {
    if (mRunnerLayoutUi == null) {
      mRunnerLayoutUi = RunnerLayoutUi.Factory.getInstance(project).create(
          "buck", "buck", "buck", project);
    }
    return mRunnerLayoutUi;
  }
}
