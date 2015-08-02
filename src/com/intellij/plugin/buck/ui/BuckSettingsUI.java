package com.intellij.plugin.buck.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import com.intellij.ui.IdeBorderFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class BuckSettingsUI extends JPanel {

  private TextFieldWithBrowseButton myBuckPathField;
  private JCheckBox myRunAfterInstall;
  private JCheckBox myMultiInstallMode;
  private JCheckBox myUninstallBeforeInstall;
  private BuckSettingsProvider myOptionsProvider;

  public BuckSettingsUI() {
    myOptionsProvider = BuckSettingsProvider.getInstance();
    init();
  }

  private void init() {
    setLayout(new BorderLayout());
    JPanel container = this;

    myBuckPathField = new TextFieldWithBrowseButton();
    FileChooserDescriptor fileChooserDescriptor =
        new FileChooserDescriptor(true, false, false, false, false, false);
    myBuckPathField.addBrowseFolderListener(
        "",
        "Buck Executable Path",
        null,
        fileChooserDescriptor,
        TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT,
        false
    );

    myRunAfterInstall = new JCheckBox("Run after install (-r)");
    myMultiInstallMode = new JCheckBox("Multi-install mode (-x)");
    myUninstallBeforeInstall = new JCheckBox("Uninstall before installing (-u)");

    JPanel buckSettings = new JPanel(new GridBagLayout());
    buckSettings.setBorder(IdeBorderFactory.createTitledBorder("Buck Settings", true));
    container.add(container = new JPanel(new BorderLayout()), BorderLayout.NORTH);
    container.add(buckSettings, BorderLayout.NORTH);
    final GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 0, 0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);

    buckSettings.add(new JLabel("Buck Executable Path:"), constraints);
    constraints.gridx = 1;
    constraints.weightx = 1;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    buckSettings.add(myBuckPathField, constraints);

    JPanel installSettings = new JPanel(new BorderLayout());
    installSettings.setBorder(IdeBorderFactory.createTitledBorder("Buck Install Settings", true));
    container.add(container = new JPanel(new BorderLayout()), BorderLayout.SOUTH);
    container.add(installSettings, BorderLayout.NORTH);

    installSettings.add(myRunAfterInstall, BorderLayout.NORTH);
    installSettings.add(installSettings = new JPanel(new BorderLayout()), BorderLayout.SOUTH);

    installSettings.add(myMultiInstallMode, BorderLayout.NORTH);
    installSettings.add(installSettings = new JPanel(new BorderLayout()), BorderLayout.SOUTH);

    installSettings.add(myUninstallBeforeInstall, BorderLayout.NORTH);
    installSettings.add(new JPanel(new BorderLayout()), BorderLayout.SOUTH);
  }

  public boolean isModified() {
    return !Comparing.equal(myBuckPathField.getText(),
        myOptionsProvider.getState().buckExecutable) ||
        myOptionsProvider.getState().runAfterInstall != myRunAfterInstall.isSelected() ||
        myOptionsProvider.getState().multiInstallMode != myMultiInstallMode.isSelected() ||
        myOptionsProvider.getState().uninstallBeforeInstalling !=
            myUninstallBeforeInstall.isSelected();
  }

  public void apply() {
    myOptionsProvider.getState().buckExecutable = myBuckPathField.getText();
    myOptionsProvider.getState().runAfterInstall = myRunAfterInstall.isSelected();
    myOptionsProvider.getState().multiInstallMode = myMultiInstallMode.isSelected();
    myOptionsProvider.getState().uninstallBeforeInstalling = myUninstallBeforeInstall.isSelected();
  }

  public void reset() {
    myBuckPathField.setText(myOptionsProvider.getState().buckExecutable);
    myRunAfterInstall.setSelected(myOptionsProvider.getState().runAfterInstall);
    myMultiInstallMode.setSelected(myOptionsProvider.getState().multiInstallMode);
    myUninstallBeforeInstall.setSelected(myOptionsProvider.getState().uninstallBeforeInstalling);
  }
}
