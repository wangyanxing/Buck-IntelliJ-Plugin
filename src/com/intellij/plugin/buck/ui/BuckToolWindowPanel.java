package com.intellij.plugin.buck.ui;

import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.openapi.project.Project;

import javax.swing.*;

public class BuckToolWindowPanel {

  public BuckToolWindowPanel(Project project) {
    this.myProject = project;
  }

  private JPanel myWholePanel;
  private JComboBox targetList;
  private JCheckBox runAfterInstallCheckBox;
  private JCheckBox uninstallBeforeInstallCheckBox;
  private JButton addTargetButton;
  private JPanel consolePanel;

  private final Project myProject;

  public JComponent createPanel() {
    return myWholePanel;
  }

  private void createUIComponents() {
    //consolePanel = new JPanel();
    //consolePanel.add(new JButton("fuck"));
    consolePanel = new ConsoleViewImpl(myProject, false);
  }
}