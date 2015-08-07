// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.impl.*;

public interface BuckTypes {

  IElementType ARRAY_ELEMENTS = new BuckElementType("ARRAY_ELEMENTS");
  IElementType EXPRESSION = new BuckElementType("EXPRESSION");
  IElementType GLOB_BLOCK = new BuckElementType("GLOB_BLOCK");
  IElementType GLOB_ELEMENTS = new BuckElementType("GLOB_ELEMENTS");
  IElementType LIST = new BuckElementType("LIST");
  IElementType LIST_ELEMENTS = new BuckElementType("LIST_ELEMENTS");
  IElementType PROPERTY = new BuckElementType("PROPERTY");
  IElementType PROPERTY_LVALUE = new BuckElementType("PROPERTY_LVALUE");
  IElementType RULE_BLOCK = new BuckElementType("RULE_BLOCK");
  IElementType RULE_BODY = new BuckElementType("RULE_BODY");
  IElementType RULE_NAME = new BuckElementType("BUCK_RULE_NAME");
  IElementType VALUE = new BuckElementType("VALUE");
  IElementType VALUE_ARRAY = new BuckElementType("VALUE_ARRAY");

  IElementType BOOLEAN = new BuckTokenType("BOOLEAN");
  IElementType COMMA = new BuckTokenType(",");
  IElementType DOUBLE_QUOTED_STRING = new BuckTokenType("DOUBLE_QUOTED_STRING");
  IElementType EQUAL = new BuckTokenType("=");
  IElementType GLOB_EXCLUDES_KEYWORD = new BuckTokenType("excludes");
  IElementType GLOB_KEYWORD = new BuckTokenType("GLOB_KEYWORD");
  IElementType IDENTIFIER = new BuckTokenType("IDENTIFIER");
  IElementType LINE_COMMENT = new BuckTokenType("LINE_COMMENT");
  IElementType L_BRACKET = new BuckTokenType("[");
  IElementType L_PARENTHESES = new BuckTokenType("(");
  IElementType MACROS = new BuckTokenType("MACROS");
  IElementType NONE = new BuckTokenType("None");
  IElementType NUMBER = new BuckTokenType("NUMBER");
  IElementType PLUS = new BuckTokenType("+");
  IElementType R_BRACKET = new BuckTokenType("]");
  IElementType R_PARENTHESES = new BuckTokenType(")");
  IElementType SINGLE_QUOTED_STRING = new BuckTokenType("SINGLE_QUOTED_STRING");
  IElementType SLASH = new BuckTokenType("\\");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ARRAY_ELEMENTS) {
        return new BuckArrayElementsImpl(node);
      }
      else if (type == EXPRESSION) {
        return new BuckExpressionImpl(node);
      }
      else if (type == GLOB_BLOCK) {
        return new BuckGlobBlockImpl(node);
      }
      else if (type == GLOB_ELEMENTS) {
        return new BuckGlobElementsImpl(node);
      }
      else if (type == LIST) {
        return new BuckListImpl(node);
      }
      else if (type == LIST_ELEMENTS) {
        return new BuckListElementsImpl(node);
      }
      else if (type == PROPERTY) {
        return new BuckPropertyImpl(node);
      }
      else if (type == PROPERTY_LVALUE) {
        return new BuckPropertyLvalueImpl(node);
      }
      else if (type == RULE_BLOCK) {
        return new BuckRuleBlockImpl(node);
      }
      else if (type == RULE_BODY) {
        return new BuckRuleBodyImpl(node);
      }
      else if (type == RULE_NAME) {
        return new BuckRuleNameImpl(node);
      }
      else if (type == VALUE) {
        return new BuckValueImpl(node);
      }
      else if (type == VALUE_ARRAY) {
        return new BuckValueArrayImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
