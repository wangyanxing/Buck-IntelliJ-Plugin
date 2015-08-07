package com.intellij.plugin.buck.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuckPsiUtils {

  public static final TokenSet STRING_LITERALS =
      TokenSet.create(BuckTypes.SINGLE_QUOTED_STRING, BuckTypes.DOUBLE_QUOTED_STRING);

  /**
   * Check that element type of the given AST node belongs to the token set.
   * <p/>
   * It slightly less verbose than {@code set.contains(node.getElementType())}
   * and overloaded methods with the same name allow check ASTNode/PsiElement against both concrete
   * element types and token sets in uniform way.
   */
  public static boolean hasElementType(@NotNull ASTNode node, @NotNull TokenSet set) {
    return set.contains(node.getElementType());
  }

  /**
   * @see #hasElementType(com.intellij.lang.ASTNode, com.intellij.psi.tree.TokenSet)
   */
  public static boolean hasElementType(@NotNull ASTNode node, IElementType... types) {
    return hasElementType(node, TokenSet.create(types));
  }

  /**
   * @see #hasElementType(com.intellij.lang.ASTNode, com.intellij.psi.tree.TokenSet)
   */
  public static boolean hasElementType(@NotNull PsiElement element, @NotNull TokenSet set) {
    return element.getNode() != null && hasElementType(element.getNode(), set);
  }

  /**
   * Test the text value of a PSI element.
   */
  public static boolean testType(PsiElement element, IElementType type) {
    return element.getNode() != null && element.getNode().getElementType() == type;
  }

  /**
   * Test the specific type of the given identifier element.
   * For it could be a BUCK_RULE_NAME or PROPERTY_LVALUE.
   */
  public static boolean testIdentifierType(PsiElement element, IElementType type) {
    if (element.getNode().getElementType() != BuckTypes.IDENTIFIER) {
      return false;
    }
    return element.getParent() != null && element.getParent().getNode().getElementType() == type;
  }

  /**
   * Find the first child with a specific type
   */
  public static PsiElement findChildWithType(@NotNull PsiElement element, IElementType type) {
    PsiElement[] children = element.getChildren();
    for (PsiElement child : children) {
      if (child.getNode().getElementType() == type) {
        return child;
      }
    }
    return null;
  }

  /**
   * Find the ancestor element with a specific type
   */
  public static PsiElement findAncestorWithType(@NotNull PsiElement element, IElementType type) {
    PsiElement parent = element.getParent();
    while (parent != null) {
      if (parent.getNode() != null && parent.getNode().getElementType() == type) {
        return parent;
      }
      parent = parent.getParent();
    }
    return null;
  }

  /**
   * Return the text content if the given BuckExpression has only one string value.
   * Return null if this expression has multiple values, for example: "a" + "b"
   */
  public static String getStringValueFromExpression(BuckExpression expression) {
    List<BuckValue> values = expression.getValueList();
    if (values.size() != 1) {
      return null;
    }
    PsiElement value = values.get(0); // This should be a "Value" type.
    if (value.getFirstChild() != null &&
        BuckPsiUtils.hasElementType(value.getFirstChild(), BuckPsiUtils.STRING_LITERALS)) {
      String text = value.getFirstChild().getText();
      return text.length() > 2 ? text.substring(1, text.length() - 1) : null;
    } else {
      return null;
    }
  }
}
