package com.intellij.plugin.buck.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.plugin.buck.lang.BuckLexerAdapter;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;

/**
 * Syntax highlighter for buck PSI elements
 */
public class BuckSyntaxHighlighter extends SyntaxHighlighterBase {

  public static final TextAttributesKey BUCK_KEYWORD = createTextAttributesKey(
      "BUCK_KEY", DefaultLanguageHighlighterColors.KEYWORD);
  public static final TextAttributesKey BUCK_NUMBER = createTextAttributesKey(
      "BUCK_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
  public static final TextAttributesKey BUCK_COMMENT = createTextAttributesKey(
      "BUCK_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
  public static final TextAttributesKey BUCK_RULE_NAME = createTextAttributesKey(
      "BUCK_RULE_NAME", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
  public static final TextAttributesKey BUCK_GLOB = createTextAttributesKey(
      "BUCK_GLOB", CodeInsightColors.ANNOTATION_NAME_ATTRIBUTES);
  public static final TextAttributesKey BUCK_STRING = createTextAttributesKey(
      "BUCK_STRING", DefaultLanguageHighlighterColors.STRING);
  public static final TextAttributesKey BUCK_MACRO = createTextAttributesKey(
      "BUCK_MACRO", DefaultLanguageHighlighterColors.STATIC_FIELD);

  private static final TextAttributesKey[] BAD_CHAR_KEYS =
      new TextAttributesKey[]{HighlighterColors.BAD_CHARACTER};
  private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{BUCK_KEYWORD};
  private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{BUCK_NUMBER};
  private static final TextAttributesKey[] GLOB_KEYS = new TextAttributesKey[]{BUCK_GLOB};
  private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{BUCK_COMMENT};
  private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{BUCK_STRING};
  private static final TextAttributesKey[] MACROS_KEYS = new TextAttributesKey[]{BUCK_MACRO};
  private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

  @Override
  public Lexer getHighlightingLexer() {
    return new BuckLexerAdapter();
  }

  @Override
  public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
    if (tokenType.equals(BuckTypes.DOUBLE_QUOTED_STRING) ||
        tokenType.equals(BuckTypes.SINGLE_QUOTED_STRING)) {
      return STRING_KEYS;
    } else if (tokenType.equals(BuckTypes.GLOB_KEYWORD) ||
        tokenType.equals(BuckTypes.GLOB_EXCLUDES_KEYWORD)) {
      return GLOB_KEYS;
    } else if (tokenType.equals(BuckTypes.BOOLEAN) ||
        tokenType.equals(BuckTypes.NONE)) {
      return KEY_KEYS;
    } else if (tokenType.equals(BuckTypes.NUMBER)) {
      return NUMBER_KEYS;
    } else if (tokenType.equals(BuckTypes.LINE_COMMENT)) {
      return COMMENT_KEYS;
    } else if (tokenType.equals(BuckTypes.MACROS)) {
      return MACROS_KEYS;
    } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
      return BAD_CHAR_KEYS;
    } else {
      return EMPTY_KEYS;
    }
  }
}
