package com.intellij.plugin.buck.config;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.plugin.buck.ui.BuckSettingsUI;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.JComponent;

public class BuckSettingsConfigurable implements SearchableConfigurable {

  private BuckSettingsUI myPanel;

  public BuckSettingsConfigurable() {
  }

  @NotNull
  @Override
  public String getId() {
    return getHelpTopic();
  }

  @Override
  public Runnable enableSearch(String option) {
    return null;
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Buck";
  }

  @Override
  public String getHelpTopic() {
    return "buck.settings";
  }

  @Override
  public JComponent createComponent() {
    myPanel = new BuckSettingsUI();
    return myPanel;
  }

  @Override
  public boolean isModified() {
    return myPanel != null && myPanel.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    if (myPanel != null) {
      myPanel.apply();
    }
  }

  @Override
  public void reset() {
    if (myPanel != null) {
      myPanel.reset();
    }
  }

  @Override
  public void disposeUIResources() {
    myPanel = null;
  }
}
