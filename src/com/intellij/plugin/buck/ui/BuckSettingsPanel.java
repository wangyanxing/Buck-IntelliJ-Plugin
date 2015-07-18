package com.intellij.plugin.buck.ui;

import com.intellij.plugin.buck.config.BuckOptionsProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BuckSettingsPanel {
  private JPanel myWholePanel;
  private BuckOptionsProvider myOptionsProvider;

  public JComponent createPanel(@NotNull BuckOptionsProvider provider) {
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
