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
    else if (t == KEYWORDS) {
      r = keywords(b, 0);
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
    else if (t == RULE_NAME) {
      r = rule_name(b, 0);
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
  // "name" |
  //              "res" |
  //              "binary_jar" |
  //              "srcs" |
  //              "deps" |
  //              "manifest" |
  //              "package_type" |
  //              "keystore" |
  //              "glob" |
  //              "visibility" |
  //              "aar" |
  //              "src_target" |
  //              "src_roots" |
  //              "java7_support" |
  //              "source_under_test" |
  //              "test_library_project_dir" |
  //              "contacts" |
  //              "exported_deps"
  public static boolean keywords(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywords")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<keywords>");
    r = consumeToken(b, "name");
    if (!r) r = consumeToken(b, "res");
    if (!r) r = consumeToken(b, "binary_jar");
    if (!r) r = consumeToken(b, "srcs");
    if (!r) r = consumeToken(b, "deps");
    if (!r) r = consumeToken(b, "manifest");
    if (!r) r = consumeToken(b, "package_type");
    if (!r) r = consumeToken(b, "keystore");
    if (!r) r = consumeToken(b, "glob");
    if (!r) r = consumeToken(b, "visibility");
    if (!r) r = consumeToken(b, "aar");
    if (!r) r = consumeToken(b, "src_target");
    if (!r) r = consumeToken(b, "src_roots");
    if (!r) r = consumeToken(b, "java7_support");
    if (!r) r = consumeToken(b, "source_under_test");
    if (!r) r = consumeToken(b, "test_library_project_dir");
    if (!r) r = consumeToken(b, "contacts");
    if (!r) r = consumeToken(b, "exported_deps");
    exit_section_(b, l, m, KEYWORDS, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ((IDENTIFIER | keywords) '=' value) | value
  public static boolean property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<property>");
    r = property_0(b, l + 1);
    if (!r) r = value(b, l + 1);
    exit_section_(b, l, m, PROPERTY, r, false, null);
    return r;
  }

  // (IDENTIFIER | keywords) '=' value
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

  // IDENTIFIER | keywords
  private static boolean property_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    if (!r) r = keywords(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // rule_name '(' (CRLF | WHITE_SPACE)* rule_body ')'
  public static boolean rule_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_block")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<rule block>");
    r = rule_name(b, l + 1);
    r = r && consumeToken(b, "(");
    r = r && rule_block_2(b, l + 1);
    r = r && rule_body(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, l, m, RULE_BLOCK, r, false, null);
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
  // "genrule"|
  //               "remote_file"|
  //               "android_aar"|
  //               "android_binary"|
  //               "android_build_config"|
  //               "android_library"|
  //               "android_manifest"|
  //               "android_prebuilt_aar"|
  //               "android_resource"|
  //               "apk_genrule"|
  //               "cxx_library"|
  //               "gen_aidl"|
  //               "ndk_library"|
  //               "prebuilt_jar"|
  //               "prebuilt_native_library"|
  //               "project_config"|
  //               "cxx_binary"|
  //               "cxx_library"|
  //               "cxx_test"|
  //               "prebuilt_native_library"|
  //               "d_binary"|
  //               "d_library"|
  //               "d_test"|
  //               "cxx_library"|
  //               "java_binary"|
  //               "java_library"|
  //               "java_test"|
  //               "prebuilt_jar"|
  //               "prebuilt_native_library"|
  //               "prebuilt_python_library"|
  //               "python_binary"|
  //               "python_library"|
  //               "python_test"|
  //               "glob"|
  //               "include_defs"|
  //               "robolectric_test"
  public static boolean rule_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule_name")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, "<rule name>");
    r = consumeToken(b, "genrule");
    if (!r) r = consumeToken(b, "remote_file");
    if (!r) r = consumeToken(b, "android_aar");
    if (!r) r = consumeToken(b, "android_binary");
    if (!r) r = consumeToken(b, "android_build_config");
    if (!r) r = consumeToken(b, "android_library");
    if (!r) r = consumeToken(b, "android_manifest");
    if (!r) r = consumeToken(b, "android_prebuilt_aar");
    if (!r) r = consumeToken(b, "android_resource");
    if (!r) r = consumeToken(b, "apk_genrule");
    if (!r) r = consumeToken(b, "cxx_library");
    if (!r) r = consumeToken(b, "gen_aidl");
    if (!r) r = consumeToken(b, "ndk_library");
    if (!r) r = consumeToken(b, "prebuilt_jar");
    if (!r) r = consumeToken(b, "prebuilt_native_library");
    if (!r) r = consumeToken(b, "project_config");
    if (!r) r = consumeToken(b, "cxx_binary");
    if (!r) r = consumeToken(b, "cxx_library");
    if (!r) r = consumeToken(b, "cxx_test");
    if (!r) r = consumeToken(b, "prebuilt_native_library");
    if (!r) r = consumeToken(b, "d_binary");
    if (!r) r = consumeToken(b, "d_library");
    if (!r) r = consumeToken(b, "d_test");
    if (!r) r = consumeToken(b, "cxx_library");
    if (!r) r = consumeToken(b, "java_binary");
    if (!r) r = consumeToken(b, "java_library");
    if (!r) r = consumeToken(b, "java_test");
    if (!r) r = consumeToken(b, "prebuilt_jar");
    if (!r) r = consumeToken(b, "prebuilt_native_library");
    if (!r) r = consumeToken(b, "prebuilt_python_library");
    if (!r) r = consumeToken(b, "python_binary");
    if (!r) r = consumeToken(b, "python_library");
    if (!r) r = consumeToken(b, "python_test");
    if (!r) r = consumeToken(b, "glob");
    if (!r) r = consumeToken(b, "include_defs");
    if (!r) r = consumeToken(b, "robolectric_test");
    exit_section_(b, l, m, RULE_NAME, r, false, null);
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
