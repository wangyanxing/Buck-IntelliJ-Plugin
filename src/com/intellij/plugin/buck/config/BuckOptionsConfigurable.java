package com.intellij.plugin.buck.config;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.plugin.buck.ui.BuckSettingsPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BuckOptionsConfigurable implements SearchableConfigurable, Configurable.NoScroll, Disposable {

  public static final String TERMINAL_SETTINGS_HELP_REFERENCE = "reference.settings.terminal";

  private BuckSettingsPanel myPanel;

  private final BuckOptionsProvider myOptionsProvider;
  private Project myProject;

  public BuckOptionsConfigurable(Project project) {
    myOptionsProvider = BuckOptionsProvider.getInstance();
    myProject = project;
  }

  @NotNull
  @Override
  public String getId() {
    return "buck";
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
    return TERMINAL_SETTINGS_HELP_REFERENCE;
  }

  @Override
  public JComponent createComponent() {
    myPanel = new BuckSettingsPanel();
    return myPanel.createPanel(myOptionsProvider);
  }

  @Override
  public boolean isModified() {
    return myPanel.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    myPanel.apply();
  }

  @Override
  public void reset() {
    myPanel.reset();
  }

  @Override
  public void disposeUIResources() {
    Disposer.dispose(this);
  }

  @Override
  public void dispose() {
    myPanel = null;
  }
}
