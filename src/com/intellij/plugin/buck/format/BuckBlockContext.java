package com.intellij.plugin.buck.format;

import com.intellij.formatting.FormattingMode;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;

public class BuckBlockContext {
  private final CommonCodeStyleSettings mySettings;
  private final SpacingBuilder mySpacingBuilder;
  private final FormattingMode myMode;

  public BuckBlockContext(CodeStyleSettings settings, SpacingBuilder builder, FormattingMode mode) {
    mySettings = settings.getCommonSettings(BuckLanguage.INSTANCE);
    mySpacingBuilder = builder;
    myMode = mode;
  }

  public CommonCodeStyleSettings getSettings() {
    return mySettings;
  }

  public SpacingBuilder getSpacingBuilder() {
    return mySpacingBuilder;
  }

  public FormattingMode getMode() {
    return myMode;
  }
}
