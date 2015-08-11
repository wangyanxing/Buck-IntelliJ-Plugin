package com.intellij.plugin.buck.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.components.JBTextField;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Buck Setting GUI, located in "Preference > Tools > Buck".
 */
public class BuckSettingsUI extends JPanel {

  public static final String CUSTOMIZED_INSTALL_COMMAND_HINT =
      "input your command here: eg. -r --no-cache";

  private TextFieldWithBrowseButton mBuckPathField;
  private JBTextField mCustomizedInstallSettingField;
  private JCheckBox mRunAfterInstall;
  private JCheckBox mMultiInstallMode;
  private JCheckBox mUninstallBeforeInstall;
  private JCheckBox mCustomizedInstallSetting;
  private BuckSettingsProvider mOptionsProvider;

  public BuckSettingsUI() {
    mOptionsProvider = BuckSettingsProvider.getInstance();
    init();
  }

  private void init() {
    setLayout(new BorderLayout());
    JPanel container = this;

    mBuckPathField = new TextFieldWithBrowseButton();
    FileChooserDescriptor fileChooserDescriptor =
        new FileChooserDescriptor(true, false, false, false, false, false);
    mBuckPathField.addBrowseFolderListener(
        "",
        "Buck Executable Path",
        null,
        fileChooserDescriptor,
        TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT,
        false
    );
    mCustomizedInstallSettingField = new JBTextField();
    mCustomizedInstallSettingField.getEmptyText().setText(CUSTOMIZED_INSTALL_COMMAND_HINT);
    mCustomizedInstallSettingField.setEnabled(false);

    mRunAfterInstall = new JCheckBox("Run after install (-r)");
    mMultiInstallMode = new JCheckBox("Multi-install mode (-x)");
    mUninstallBeforeInstall = new JCheckBox("Uninstall before installing (-u)");
    mCustomizedInstallSetting = new JCheckBox("Use customized install setting:  ");
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
    buckSettings.add(mBuckPathField, constraints);

    JPanel installSettings = new JPanel(new BorderLayout());
    installSettings.setBorder(IdeBorderFactory.createTitledBorder("Buck Install Settings", true));
    container.add(container = new JPanel(new BorderLayout()), BorderLayout.SOUTH);
    container.add(installSettings, BorderLayout.NORTH);

    installSettings.add(mRunAfterInstall, BorderLayout.NORTH);
    installSettings.add(installSettings = new JPanel(new BorderLayout()), BorderLayout.SOUTH);

    installSettings.add(mMultiInstallMode, BorderLayout.NORTH);
    installSettings.add(installSettings = new JPanel(new BorderLayout()), BorderLayout.SOUTH);

    installSettings.add(mUninstallBeforeInstall, BorderLayout.NORTH);
    installSettings.add(installSettings = new JPanel(new BorderLayout()), BorderLayout.SOUTH);

    final GridBagConstraints customConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0,
        GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    JPanel customizedInstallSetting = new JPanel(new GridBagLayout());
    customizedInstallSetting.add(mCustomizedInstallSetting, customConstraints);
    customConstraints.gridx = 1;
    customConstraints.weightx = 1;
    customConstraints.fill = GridBagConstraints.HORIZONTAL;
    customizedInstallSetting.add(mCustomizedInstallSettingField, customConstraints);
    installSettings.add(customizedInstallSetting, BorderLayout.NORTH);
  }

  public boolean isModified() {
    return !Comparing.equal(mBuckPathField.getText(),
        mOptionsProvider.getState().buckExecutable) ||
        mOptionsProvider.getState().runAfterInstall != mRunAfterInstall.isSelected() ||
        mOptionsProvider.getState().multiInstallMode != mMultiInstallMode.isSelected() ||
        mOptionsProvider.getState().uninstallBeforeInstalling !=
            mUninstallBeforeInstall.isSelected() ||
        mOptionsProvider.getState().customizedInstallSetting !=
            mCustomizedInstallSetting.isSelected() ||
        !mOptionsProvider.getState().customizedInstallSettingCommand
            .equals(mCustomizedInstallSettingField.getText());
  }

  public void apply() {
    mOptionsProvider.getState().buckExecutable = mBuckPathField.getText();
    mOptionsProvider.getState().runAfterInstall = mRunAfterInstall.isSelected();
    mOptionsProvider.getState().multiInstallMode = mMultiInstallMode.isSelected();
    mOptionsProvider.getState().uninstallBeforeInstalling = mUninstallBeforeInstall.isSelected();
    mOptionsProvider.getState().customizedInstallSetting = mCustomizedInstallSetting.isSelected();
    mOptionsProvider.getState().customizedInstallSettingCommand =
        mCustomizedInstallSettingField.getText();
  }

  public void reset() {
    mBuckPathField.setText(mOptionsProvider.getState().buckExecutable);
    mRunAfterInstall.setSelected(mOptionsProvider.getState().runAfterInstall);
    mMultiInstallMode.setSelected(mOptionsProvider.getState().multiInstallMode);
    mUninstallBeforeInstall.setSelected(mOptionsProvider.getState().uninstallBeforeInstalling);
    mCustomizedInstallSetting.setSelected(mOptionsProvider.getState().customizedInstallSetting);
    mCustomizedInstallSettingField.setText(
        mOptionsProvider.getState().customizedInstallSettingCommand);
  }

  private void initCustomizedInstallCommandListener() {
    mCustomizedInstallSetting.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
          mCustomizedInstallSettingField.setEnabled(true);
          mRunAfterInstall.setEnabled(false);
          mMultiInstallMode.setEnabled(false);
          mUninstallBeforeInstall.setEnabled(false);
        } else {
          mCustomizedInstallSettingField.setEnabled(false);
          mRunAfterInstall.setEnabled(true);
          mMultiInstallMode.setEnabled(true);
          mUninstallBeforeInstall.setEnabled(true);
        }
      }
    });
  }
}
