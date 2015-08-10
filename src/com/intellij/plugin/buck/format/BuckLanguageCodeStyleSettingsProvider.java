package com.intellij.plugin.buck.format;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.plugin.buck.file.BuckFileUtil;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;

/**
 * BUCK code style settings
 */
public class BuckLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

  @Override
  public Language getLanguage() {
    return BuckLanguage.INSTANCE;
  }

  @Override
  public IndentOptionsEditor getIndentOptionsEditor() {
    return new SmartIndentOptionsEditor();
  }

  @Override
  public CommonCodeStyleSettings getDefaultCommonSettings() {
    CommonCodeStyleSettings defaultSettings = new CommonCodeStyleSettings(BuckLanguage.INSTANCE);
    CommonCodeStyleSettings.IndentOptions indentOptions = defaultSettings.initIndentOptions();

    indentOptions.INDENT_SIZE = 2;
    indentOptions.TAB_SIZE = 2;
    indentOptions.CONTINUATION_INDENT_SIZE = 2;

    defaultSettings.ALIGN_MULTILINE_PARAMETERS_IN_CALLS = true;
    defaultSettings.KEEP_BLANK_LINES_IN_DECLARATIONS = 1;
    defaultSettings.KEEP_BLANK_LINES_IN_CODE = 1;
    defaultSettings.RIGHT_MARGIN = 100;
    return defaultSettings;
  }

  @Override
  public String getCodeSample(SettingsType settingsType) {
    return BuckFileUtil.getSampleBuckFile();
  }
}