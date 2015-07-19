package com.intellij.plugin.buck.format;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import org.jetbrains.annotations.NotNull;

public class BuckLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {

  @NotNull
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
    indentOptions.INDENT_SIZE = 4;
    defaultSettings.ALIGN_MULTILINE_PARAMETERS_IN_CALLS = true;
    defaultSettings.KEEP_BLANK_LINES_IN_DECLARATIONS = 1;
    defaultSettings.KEEP_BLANK_LINES_IN_CODE = 1;
    defaultSettings.RIGHT_MARGIN = 99;
    return defaultSettings;
  }

  @Override
  public String getCodeSample(@NotNull SettingsType settingsType) {
    return "# Thanks for installing IntelliJ IEDA Buck Plugin!\n" +
        "android_library(\n" +
        "  name = 'adinterfaces',\n" +
        "  srcs = glob(['**/*.java']),\n" +
        "  deps = [\n" +
        "    '//android_res/com/facebook/adinterfaces:res',\n" +
        "    '//android_res/com/facebook/common/strings:res',\n" +
        "    '//android_res/com/facebook/custom:res'\n" +
        "  ],\n" +
        "  visibility = [\n" +
        "    'PUBLIC',\n" +
        "  ],\n\n" +
        "project_config(" +
        "  src_target = ':adinterfaces'," +
        ")";
  }

}