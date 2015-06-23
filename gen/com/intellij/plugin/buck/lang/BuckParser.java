// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.intellij.plugin.buck.lang.psi.BuckTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class BuckParser implements PsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == ARRAY_ELEMENTS) {
      r = array_elements(b, 0);
    }
    else if (t == BRACES) {
      r = braces(b, 0);
    }
    else if (t == COMMA) {
      r = comma(b, 0);
    }
    else if (t == EQUAL) {
      r = equal(b, 0);
    }
    else if (t == PROPERTY) {
      r = property(b, 0);
    }
    else if (t == RULE_BLOCK) {
      r = rule_block(b, 0);
    }
    else if (t == RULE_BODY) {
      r = rule_body(b, 0);
    }
    else if (t == SEMICOLON) {
      r = semicolon(b, 0);
    }
    else if (t == VALUE) {
      r = value(b, 0);
    }
    else if (t == VALUE_ARRAY) {
      r = value_array(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return buckFile(b, l + 1);
  }

  /* ********************************************************** */
  // (
  //   value |
  //   "," |
  //   CRLF |
  //   WHITE_SPACE
  // )*
  public static boolean array_elements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<array elements>");
    int c = current_position_(b);
    while (true) {
      if (!array_elements_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_elements", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, ARRAY_ELEMENTS, true, false, null);
    return true;
  }

  // value |
  //   "," |
  //   CRLF |
  //   WHITE_SPACE
  private static boolean array_elements_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    if (!r) r = consumeToken(b, ",");
    if (!r) r = consumeToken(b, CRLF);
    if (!r) r = consumeToken(b, WHITE_SPACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' | ')' | '{' | '}' | '[' | ']'
  public static boolean braces(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "braces")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<braces>");
    r = consumeToken(b, "(");
    if (!r) r = consumeToken(b, ")");
    if (!r) r = consumeToken(b, "{");
    if (!r) r = consumeToken(b, "}");
    if (!r) r = consumeToken(b, "[");
    if (!r) r = consumeToken(b, "]");
    exit_section_(b, l, m, BRACES, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // item_*
  static boolean buckFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "buckFile")) return false;
    int c = current_position_(b);
    while (true) {
      if (!item_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "buckFile", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  /* ********************************************************** */
  // ','
  public static boolean comma(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comma")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<comma>");
    r = consumeToken(b, ",");
    exit_section_(b, l, m, COMMA, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '='
  public static boolean equal(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "equal")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<equal>");
    r = consumeToken(b, "=");
    exit_section_(b, l, m, EQUAL, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // rule_block | property | COMMENT | CRLF
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_block(b, l + 1);
    if (!r) r = property(b, l + 1);
    if (!r) r = consumeToken(b, COMMENT);
    if (!r) r = consumeToken(b, CRLF);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ((IDENTIFIER | KEYWORDS) '=' value) | value
  public static boolean property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<property>");
    r = property_0(b, l + 1);
    if (!r) r = value(b, l + 1);
    exit_section_(b, l, m, PROPERTY, r, false, null);
    return r;
  }

  // (IDENTIFIER | KEYWORDS) '=' value
  private static boolean property_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property_0_0(b, l + 1);
    r = r && consumeToken(b, "=");
    r = r && value(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // IDENTIFIER | KEYWORDS
  private static boolean property_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, KEYWORDS);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (RULE_NAMES | GENERIC_RULE_NAMES | IDENTIFIER) '(' (CRLF | WHITE_SPACE)* rule_body ')'
  public static boolean rule_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_block")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<rule block>");
    r = rule_block_0(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && rule_block_2(b, l + 1);
    r = r && rule_body(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, RULE_BLOCK, r, false, null);
    return r;
  }

  // RULE_NAMES | GENERIC_RULE_NAMES | IDENTIFIER
  private static boolean rule_block_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_block_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RULE_NAMES);
    if (!r) r = consumeToken(b, GENERIC_RULE_NAMES);
    if (!r) r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CRLF | WHITE_SPACE)*
  private static boolean rule_block_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_block_2")) return false;
    int c = current_position_(b);
    while (true) {
      if (!rule_block_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "rule_block_2", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // CRLF | WHITE_SPACE
  private static boolean rule_block_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_block_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    if (!r) r = consumeToken(b, WHITE_SPACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (
  //   property  |
  //   "," |
  //   CRLF |
  //   WHITE_SPACE
  // )*
  public static boolean rule_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body")) return false;
    Marker m = enter_section_(b, l, _NONE_, "<rule body>");
    int c = current_position_(b);
    while (true) {
      if (!rule_body_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "rule_body", c)) break;
      c = current_position_(b);
    }
    exit_section_(b, l, m, RULE_BODY, true, false, null);
    return true;
  }

  // property  |
  //   "," |
  //   CRLF |
  //   WHITE_SPACE
  private static boolean rule_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property(b, l + 1);
    if (!r) r = consumeToken(b, ",");
    if (!r) r = consumeToken(b, CRLF);
    if (!r) r = consumeToken(b, WHITE_SPACE);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // ';'
  public static boolean semicolon(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "semicolon")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<semicolon>");
    r = consumeToken(b, ";");
    exit_section_(b, l, m, SEMICOLON, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // VALUE_STRING | VALUE_BOOLEAN | IDENTIFIER | value_array | rule_block
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<value>");
    r = consumeToken(b, VALUE_STRING);
    if (!r) r = consumeToken(b, VALUE_BOOLEAN);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = value_array(b, l + 1);
    if (!r) r = rule_block(b, l + 1);
    exit_section_(b, l, m, VALUE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' (CRLF | WHITE_SPACE)* array_elements ']'
  public static boolean value_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_array")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<value array>");
    r = consumeToken(b, "[");
    r = r && value_array_1(b, l + 1);
    r = r && array_elements(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, VALUE_ARRAY, r, false, null);
    return r;
  }

  // (CRLF | WHITE_SPACE)*
  private static boolean value_array_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_array_1")) return false;
    int c = current_position_(b);
    while (true) {
      if (!value_array_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "value_array_1", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // CRLF | WHITE_SPACE
  private static boolean value_array_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_array_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    if (!r) r = consumeToken(b, WHITE_SPACE);
    exit_section_(b, m, null, r);
    return r;
  }

}
