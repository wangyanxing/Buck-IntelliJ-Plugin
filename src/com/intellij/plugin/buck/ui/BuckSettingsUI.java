package com.intellij.plugin.buck.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import com.intellij.ui.IdeBorderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class BuckSettingsUI extends JPanel {

  private static final String CUSTOMIZED_INSTALL_COMMAND_HINT = "input your command here: eg. -r --no-cache";

  private TextFieldWithBrowseButton myBuckPathField;
  private JTextField myCustomizedInstallSettingField;
  private JCheckBox myRunAfterInstall;
  private JCheckBox myMultiInstallMode;
  private JCheckBox myUninstallBeforeInstall;
  private JCheckBox myCustomizedInstallSetting;
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
    myCustomizedInstallSettingField = new JTextField(CUSTOMIZED_INSTALL_COMMAND_HINT);
    myCustomizedInstallSettingField.setEnabled(false);

    myRunAfterInstall = new JCheckBox("Run after install (-r)");
    myMultiInstallMode = new JCheckBox("Multi-install mode (-x)");
    myUninstallBeforeInstall = new JCheckBox("Uninstall before installing (-u)");
    myCustomizedInstallSetting = new JCheckBox("Use customized install setting:  ");
    initCustomizedInstallCommandListener();

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
    installSettings.add(installSettings = new JPanel(new BorderLayout()), BorderLayout.SOUTH);

    final GridBagConstraints constraints1 = new GridBagConstraints(0, 0, 1, 1, 0, 0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    JPanel customizedInstallSetting = new JPanel(new GridBagLayout());
    customizedInstallSetting.add(myCustomizedInstallSetting, constraints1);
    constraints1.gridx = 1;
    constraints1.weightx = 1;
    constraints1.fill = GridBagConstraints.HORIZONTAL;
    customizedInstallSetting.add(myCustomizedInstallSettingField, constraints1);
    installSettings.add(customizedInstallSetting, BorderLayout.WEST);
  }

  public boolean isModified() {
    return !Comparing.equal(myBuckPathField.getText(),
        myOptionsProvider.getState().buckExecutable) ||
        myOptionsProvider.getState().runAfterInstall != myRunAfterInstall.isSelected() ||
        myOptionsProvider.getState().multiInstallMode != myMultiInstallMode.isSelected() ||
        myOptionsProvider.getState().uninstallBeforeInstalling !=
            myUninstallBeforeInstall.isSelected() ||
        myOptionsProvider.getState().customizedInstallSetting !=
            myCustomizedInstallSetting.isSelected() ||
        !myOptionsProvider.getState().customizedInstallSettingCommand
            .equals(myCustomizedInstallSettingField.getText());
  }

  public void apply() {
    myOptionsProvider.getState().buckExecutable = myBuckPathField.getText();
    myOptionsProvider.getState().runAfterInstall = myRunAfterInstall.isSelected();
    myOptionsProvider.getState().multiInstallMode = myMultiInstallMode.isSelected();
    myOptionsProvider.getState().uninstallBeforeInstalling = myUninstallBeforeInstall.isSelected();
    myOptionsProvider.getState().customizedInstallSetting = myCustomizedInstallSetting.isSelected();
    myOptionsProvider.getState().customizedInstallSettingCommand = myCustomizedInstallSettingField.getText();
  }

  public void reset() {
    myBuckPathField.setText(myOptionsProvider.getState().buckExecutable);
    myRunAfterInstall.setSelected(myOptionsProvider.getState().runAfterInstall);
    myMultiInstallMode.setSelected(myOptionsProvider.getState().multiInstallMode);
    myUninstallBeforeInstall.setSelected(myOptionsProvider.getState().uninstallBeforeInstalling);
    myCustomizedInstallSetting.setSelected(myOptionsProvider.getState().customizedInstallSetting);
    myCustomizedInstallSettingField.setText(myOptionsProvider.getState().customizedInstallSettingCommand);
  }

  private void initCustomizedInstallCommandListener() {
    myCustomizedInstallSetting.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          myCustomizedInstallSettingField.setEnabled(true);
          myRunAfterInstall.setEnabled(false);
          myMultiInstallMode.setEnabled(false);
          myUninstallBeforeInstall.setEnabled(false);
        } else {
          myCustomizedInstallSettingField.setEnabled(false);
          if (myCustomizedInstallSettingField.getText().equals("")) {
            myCustomizedInstallSettingField.setText(CUSTOMIZED_INSTALL_COMMAND_HINT);
          }
          myRunAfterInstall.setEnabled(true);
          myMultiInstallMode.setEnabled(true);
          myUninstallBeforeInstall.setEnabled(true);
        }
      }
    });
    myCustomizedInstallSettingField.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (myCustomizedInstallSettingField.getText().equals(CUSTOMIZED_INSTALL_COMMAND_HINT)
            && myCustomizedInstallSettingField.isEnabled()) {
          myCustomizedInstallSettingField.setText("");
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {

      }

      @Override
      public void mouseExited(MouseEvent e) {

      }
    });
  }
}
