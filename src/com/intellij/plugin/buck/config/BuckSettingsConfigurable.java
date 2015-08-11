package com.intellij.plugin.buck.config;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.plugin.buck.ui.BuckSettingsUI;

import javax.swing.JComponent;

public class BuckSettingsConfigurable implements SearchableConfigurable {

  private BuckSettingsUI mPanel;

  public BuckSettingsConfigurable() {
  }

  @Override
  public String getId() {
    return getHelpTopic();
  }

  @Override
  public Runnable enableSearch(String option) {
    return null;
  }

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
    mPanel = new BuckSettingsUI();
    return mPanel;
  }

  @Override
  public boolean isModified() {
    return mPanel != null && mPanel.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    if (mPanel != null) {
      mPanel.apply();
    }
  }

  @Override
  public void reset() {
    if (mPanel != null) {
      mPanel.reset();
    }
  }

  @Override
  public void disposeUIResources() {
    mPanel = null;
  }
}
