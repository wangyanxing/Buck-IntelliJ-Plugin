// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.impl.*;

public interface BuckTypes {

  IElementType ARRAY_ELEMENTS = new BuckElementType("ARRAY_ELEMENTS");
  IElementType BRACES = new BuckElementType("BRACES");
  IElementType COMMA = new BuckElementType("COMMA");
  IElementType EQUAL = new BuckElementType("EQUAL");
  IElementType PROPERTY = new BuckElementType("PROPERTY");
  IElementType RULE_BLOCK = new BuckElementType("RULE_BLOCK");
  IElementType RULE_BODY = new BuckElementType("RULE_BODY");
  IElementType SEMICOLON = new BuckElementType("SEMICOLON");
  IElementType VALUE = new BuckElementType("VALUE");
  IElementType VALUE_ARRAY = new BuckElementType("VALUE_ARRAY");

  IElementType COMMENT = new BuckTokenType("COMMENT");
  IElementType GENERIC_RULE_NAMES = new BuckTokenType("GENERIC_RULE_NAMES");
  IElementType IDENTIFIER = new BuckTokenType("IDENTIFIER");
  IElementType KEYWORDS = new BuckTokenType("KEYWORDS");
  IElementType MACROS = new BuckTokenType("MACROS");
  IElementType RULE_NAMES = new BuckTokenType("RULE_NAMES");
  IElementType VALUE_BOOLEAN = new BuckTokenType("VALUE_BOOLEAN");
  IElementType VALUE_STRING = new BuckTokenType("VALUE_STRING");
  IElementType WHITE_SPACE = new BuckTokenType("WHITE_SPACE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == ARRAY_ELEMENTS) {
        return new BuckArrayElementsImpl(node);
      }
      else if (type == BRACES) {
        return new BuckBracesImpl(node);
      }
      else if (type == COMMA) {
        return new BuckCommaImpl(node);
      }
      else if (type == EQUAL) {
        return new BuckEqualImpl(node);
      }
      else if (type == PROPERTY) {
        return new BuckPropertyImpl(node);
      }
      else if (type == RULE_BLOCK) {
        return new BuckRuleBlockImpl(node);
      }
      else if (type == RULE_BODY) {
        return new BuckRuleBodyImpl(node);
      }
      else if (type == SEMICOLON) {
        return new BuckSemicolonImpl(node);
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
