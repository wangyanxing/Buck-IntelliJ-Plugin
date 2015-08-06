package com.intellij.plugin.buck.config;


import com.intellij.application.options.CodeStyleAbstractConfigurable;
import com.intellij.application.options.CodeStyleAbstractPanel;
import com.intellij.application.options.TabbedLanguageCodeStylePanel;
import com.intellij.openapi.options.Configurable;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuckCodeStyleSettingsProvider extends CodeStyleSettingsProvider {
  @Override
  public CustomCodeStyleSettings createCustomSettings(CodeStyleSettings settings) {
    return new BuckCodeStyleSettings(settings);
  }

  @Nullable
  @Override
  public String getConfigurableDisplayName() {
    return "Buck";
  }

  @NotNull
  @Override
  public Configurable createSettingsPage(
      CodeStyleSettings settings, CodeStyleSettings originalSettings) {
    return new CodeStyleAbstractConfigurable(settings, originalSettings, "Buck") {
      @Override
      protected CodeStyleAbstractPanel createPanel(CodeStyleSettings settings) {
        return new BuckCodeStyleMainPanel(getCurrentSettings(), settings);
      }

      @Nullable
      @Override
      public String getHelpTopic() {
        return null;
      }
    };
  }

  private static class BuckCodeStyleMainPanel extends TabbedLanguageCodeStylePanel {
    public BuckCodeStyleMainPanel(CodeStyleSettings currentSettings, CodeStyleSettings settings) {
      super(BuckLanguage.INSTANCE, currentSettings, settings);
    }
  }
}