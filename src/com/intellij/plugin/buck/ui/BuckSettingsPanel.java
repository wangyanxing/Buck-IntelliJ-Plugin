package com.intellij.plugin.buck.ui;

import com.intellij.plugin.buck.config.BuckSettingsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BuckSettingsPanel {

  private JPanel myWholePanel;
  private BuckSettingsProvider myOptionsProvider;

  public JComponent createPanel(@NotNull BuckSettingsProvider provider) {
    myOptionsProvider = provider;
    return myWholePanel;
  }

  public boolean isModified() {
    return false;
  }

  public void apply() {
  }

  public void reset() {
  }
}
