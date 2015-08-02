package com.intellij.plugin.buck.ui;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Comparing;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BuckSettingsPanel {

  private JPanel myWholePanel;
  private TextFieldWithBrowseButton myBuckPathField;
  private BuckSettingsProvider myOptionsProvider;

  public JComponent createPanel(@NotNull BuckSettingsProvider provider) {
    myOptionsProvider = provider;

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

    return myWholePanel;
  }

  public boolean isModified() {
    return !Comparing.equal(myBuckPathField.getText(), myOptionsProvider.getState().buckExecutable);
  }

  public void apply() {
    myOptionsProvider.getState().buckExecutable = myBuckPathField.getText();
  }

  public void reset() {
    myBuckPathField.setText(myOptionsProvider.getState().buckExecutable);
  }
}
