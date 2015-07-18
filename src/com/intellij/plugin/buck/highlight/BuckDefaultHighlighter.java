package com.intellij.plugin.buck.highlight;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

public class BuckDefaultHighlighter {

  static final TextAttributes TEXT_ATTR = new TextAttributes(new Color(0, 0, 0), null, null, null, Font.PLAIN);
  static final TextAttributes COMMENT_ATTR = DefaultLanguageHighlighterColors.LINE_COMMENT.getDefaultAttributes();
  static final TextAttributes STRING_ATTR = DefaultLanguageHighlighterColors.STRING.getDefaultAttributes();
  static final TextAttributes WRONG_STRING_ATTR = DefaultLanguageHighlighterColors.STRING.getDefaultAttributes();
  static final TextAttributes NUMBER_ATTR = DefaultLanguageHighlighterColors.NUMBER.getDefaultAttributes();
  static final TextAttributes BAD_CHARACTER_ATTR = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes()
      .clone();
  static final TextAttributes KEYWORD_ATTR = DefaultLanguageHighlighterColors.KEYWORD.getDefaultAttributes().clone();
  static final TextAttributes ENUM_CONSTANT_ATTR = CodeInsightColors.STATIC_FIELD_ATTRIBUTES.getDefaultAttributes()
      .clone();
  static final TextAttributes ERROR_INFO_ATTR = CodeInsightColors.WRONG_REFERENCES_ATTRIBUTES.getDefaultAttributes()
      .clone();

  static { // Apply attribute customizations.
    BAD_CHARACTER_ATTR.setForegroundColor(new Color(255, 0, 0));
    ENUM_CONSTANT_ATTR.setFontType(Font.BOLD);
  }

  public static TextAttributesKey TEXT_ATTR_KEY = TextAttributesKey.createTextAttributesKey("default text",
      TEXT_ATTR);
  public static TextAttributesKey LINE_COMMENT_ATTR_KEY = TextAttributesKey.createTextAttributesKey("line comment",
      COMMENT_ATTR);
  public static TextAttributesKey STRING_ATTR_KEY = TextAttributesKey.createTextAttributesKey("string", STRING_ATTR);
  public static TextAttributesKey WRONG_STRING_ATTR_KEY = TextAttributesKey.createTextAttributesKey("wrong string",
      WRONG_STRING_ATTR);
  public static TextAttributesKey BAD_CHARACTER_ATTR_KEY = TextAttributesKey.createTextAttributesKey("bad character",
      BAD_CHARACTER_ATTR);
  public static TextAttributesKey NUMBER_ATTR_KEY = TextAttributesKey.createTextAttributesKey("number", NUMBER_ATTR);
  public static TextAttributesKey KEYWORD_ATTR_KEY = TextAttributesKey.createTextAttributesKey("keyword",
      KEYWORD_ATTR);
  public static TextAttributesKey ENUM_CONSTANT_ATTR_KEY = TextAttributesKey.createTextAttributesKey("enum constant",
      ENUM_CONSTANT_ATTR);
  public static TextAttributesKey ERROR_INFO_ATTR_KEY = TextAttributesKey.createTextAttributesKey("error",
      ERROR_INFO_ATTR);
}
