package com.intellij.plugin.buck.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

public class BuckDefaultHighlighter {

  static final TextAttributes BAD_CHARACTER_ATTR = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes()
      .clone();
  static final TextAttributes ENUM_CONSTANT_ATTR = CodeInsightColors.STATIC_FIELD_ATTRIBUTES.getDefaultAttributes()
      .clone();

  // Apply attribute customizations.
  static {
    BAD_CHARACTER_ATTR.setForegroundColor(new Color(255, 0, 0));
    ENUM_CONSTANT_ATTR.setFontType(Font.BOLD);
  }
}
