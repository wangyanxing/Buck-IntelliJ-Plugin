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
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class BuckParser implements PsiParser, LightPsiParser {

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
    else if (t == EXPRESSION) {
      r = expression(b, 0);
    }
    else if (t == GLOB_BLOCK) {
      r = glob_block(b, 0);
    }
    else if (t == GLOB_ELEMENTS) {
      r = glob_elements(b, 0);
    }
    else if (t == LIST) {
      r = list(b, 0);
    }
    else if (t == LIST_ELEMENTS) {
      r = list_elements(b, 0);
    }
    else if (t == PROPERTY) {
      r = property(b, 0);
    }
    else if (t == PROPERTY_LVALUE) {
      r = property_lvalue(b, 0);
    }
    else if (t == RULE_BLOCK) {
      r = rule_block(b, 0);
    }
    else if (t == RULE_BODY) {
      r = rule_body(b, 0);
    }
    else if (t == RULE_CALL) {
      r = rule_call(b, 0);
    }
    else if (t == RULE_NAME) {
      r = rule_name(b, 0);
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
  // (value ',')* [value [',']]
  public static boolean array_elements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<array elements>");
    r = array_elements_0(b, l + 1);
    r = r && array_elements_1(b, l + 1);
    exit_section_(b, l, m, ARRAY_ELEMENTS, r, false, null);
    return r;
  }

  // (value ',')*
  private static boolean array_elements_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!array_elements_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "array_elements_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // value ','
  private static boolean array_elements_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    r = r && consumeToken(b, COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  // [value [',']]
  private static boolean array_elements_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements_1")) return false;
    array_elements_1_0(b, l + 1);
    return true;
  }

  // value [',']
  private static boolean array_elements_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    r = r && array_elements_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [',']
  private static boolean array_elements_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "array_elements_1_0_1")) return false;
    consumeToken(b, COMMA);
    return true;
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
  // (value operator)* [value]
  public static boolean expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = expression_0(b, l + 1);
    r = r && expression_1(b, l + 1);
    exit_section_(b, l, m, EXPRESSION, r, false, null);
    return r;
  }

  // (value operator)*
  private static boolean expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!expression_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // value operator
  private static boolean expression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    r = r && operator(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [value]
  private static boolean expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_1")) return false;
    value(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // GLOB_KEYWORD '(' glob_elements ')'
  public static boolean glob_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glob_block")) return false;
    if (!nextTokenIs(b, GLOB_KEYWORD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GLOB_KEYWORD);
    r = r && consumeToken(b, L_PARENTHESES);
    r = r && glob_elements(b, l + 1);
    r = r && consumeToken(b, R_PARENTHESES);
    exit_section_(b, m, GLOB_BLOCK, r);
    return r;
  }

  /* ********************************************************** */
  // value_array [',' GLOB_EXCLUDES_KEYWORD '=' expression]
  public static boolean glob_elements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glob_elements")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value_array(b, l + 1);
    r = r && glob_elements_1(b, l + 1);
    exit_section_(b, m, GLOB_ELEMENTS, r);
    return r;
  }

  // [',' GLOB_EXCLUDES_KEYWORD '=' expression]
  private static boolean glob_elements_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glob_elements_1")) return false;
    glob_elements_1_0(b, l + 1);
    return true;
  }

  // ',' GLOB_EXCLUDES_KEYWORD '=' expression
  private static boolean glob_elements_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "glob_elements_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMA);
    r = r && consumeToken(b, GLOB_EXCLUDES_KEYWORD);
    r = r && consumeToken(b, EQUAL);
    r = r && expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // rule_call | rule_block | property | LINE_COMMENT
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_call(b, l + 1);
    if (!r) r = rule_block(b, l + 1);
    if (!r) r = property(b, l + 1);
    if (!r) r = consumeToken(b, LINE_COMMENT);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' list_elements ')'
  public static boolean list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list")) return false;
    if (!nextTokenIs(b, L_PARENTHESES)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_PARENTHESES);
    r = r && list_elements(b, l + 1);
    r = r && consumeToken(b, R_PARENTHESES);
    exit_section_(b, m, LIST, r);
    return r;
  }

  /* ********************************************************** */
  // (value ',')* [value [',']]
  public static boolean list_elements(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_elements")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<list elements>");
    r = list_elements_0(b, l + 1);
    r = r && list_elements_1(b, l + 1);
    exit_section_(b, l, m, LIST_ELEMENTS, r, false, null);
    return r;
  }

  // (value ',')*
  private static boolean list_elements_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_elements_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!list_elements_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "list_elements_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // value ','
  private static boolean list_elements_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_elements_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    r = r && consumeToken(b, COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  // [value [',']]
  private static boolean list_elements_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_elements_1")) return false;
    list_elements_1_0(b, l + 1);
    return true;
  }

  // value [',']
  private static boolean list_elements_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_elements_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = value(b, l + 1);
    r = r && list_elements_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [',']
  private static boolean list_elements_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "list_elements_1_0_1")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // PLUS
  static boolean operator(PsiBuilder b, int l) {
    return consumeToken(b, PLUS);
  }

  /* ********************************************************** */
  // property_lvalue '=' expression
  public static boolean property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property")) return false;
    if (!nextTokenIs(b, "<property>", IDENTIFIER, MACROS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<property>");
    r = property_lvalue(b, l + 1);
    r = r && consumeToken(b, EQUAL);
    r = r && expression(b, l + 1);
    exit_section_(b, l, m, PROPERTY, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER | MACROS
  public static boolean property_lvalue(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_lvalue")) return false;
    if (!nextTokenIs(b, "<property lvalue>", IDENTIFIER, MACROS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<property lvalue>");
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, MACROS);
    exit_section_(b, l, m, PROPERTY_LVALUE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // rule_name '(' rule_body ')'
  public static boolean rule_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_block")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_name(b, l + 1);
    r = r && consumeToken(b, L_PARENTHESES);
    r = r && rule_body(b, l + 1);
    r = r && consumeToken(b, R_PARENTHESES);
    exit_section_(b, m, RULE_BLOCK, r);
    return r;
  }

  /* ********************************************************** */
  // (property ',')* [property [',']]
  public static boolean rule_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<rule body>");
    r = rule_body_0(b, l + 1);
    r = r && rule_body_1(b, l + 1);
    exit_section_(b, l, m, RULE_BODY, r, false, null);
    return r;
  }

  // (property ',')*
  private static boolean rule_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body_0")) return false;
    int c = current_position_(b);
    while (true) {
      if (!rule_body_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "rule_body_0", c)) break;
      c = current_position_(b);
    }
    return true;
  }

  // property ','
  private static boolean rule_body_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property(b, l + 1);
    r = r && consumeToken(b, COMMA);
    exit_section_(b, m, null, r);
    return r;
  }

  // [property [',']]
  private static boolean rule_body_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body_1")) return false;
    rule_body_1_0(b, l + 1);
    return true;
  }

  // property [',']
  private static boolean rule_body_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = property(b, l + 1);
    r = r && rule_body_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // [',']
  private static boolean rule_body_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_body_1_0_1")) return false;
    consumeToken(b, COMMA);
    return true;
  }

  /* ********************************************************** */
  // rule_name '(' list_elements ')'
  public static boolean rule_call(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_call")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule_name(b, l + 1);
    r = r && consumeToken(b, L_PARENTHESES);
    r = r && list_elements(b, l + 1);
    r = r && consumeToken(b, R_PARENTHESES);
    exit_section_(b, m, RULE_CALL, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean rule_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_name")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, RULE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING |
  //           NONE | BOOLEAN | NUMBER | MACROS | value_array | list | glob_block
  public static boolean value(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<value>");
    r = consumeToken(b, DOUBLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, SINGLE_QUOTED_STRING);
    if (!r) r = consumeToken(b, NONE);
    if (!r) r = consumeToken(b, BOOLEAN);
    if (!r) r = consumeToken(b, NUMBER);
    if (!r) r = consumeToken(b, MACROS);
    if (!r) r = value_array(b, l + 1);
    if (!r) r = list(b, l + 1);
    if (!r) r = glob_block(b, l + 1);
    exit_section_(b, l, m, VALUE, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // '[' array_elements ']'
  public static boolean value_array(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "value_array")) return false;
    if (!nextTokenIs(b, L_BRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, L_BRACKET);
    r = r && array_elements(b, l + 1);
    r = r && consumeToken(b, R_BRACKET);
    exit_section_(b, m, VALUE_ARRAY, r);
    return r;
  }

}
