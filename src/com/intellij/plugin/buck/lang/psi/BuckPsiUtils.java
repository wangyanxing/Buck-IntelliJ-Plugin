package com.intellij.plugin.buck.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class BuckPsiUtils {
  /**
   * Check that element type of the given AST node belongs to the token set.
   * <p/>
   * It slightly less verbose than {@code set.contains(node.getElementType())} and overloaded methods with the same name
   * allow check ASTNode/PsiElement against both concrete element types and token sets in uniform way.
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
   * @see #hasElementType(com.intellij.lang.ASTNode, com.intellij.psi.tree.IElementType...)
   */
  public static boolean hasElementType(@NotNull PsiElement element, IElementType... types) {
    return element.getNode() != null && hasElementType(element.getNode(), types);
  }

  ///////////////////////////
  public static String getKey(BuckProperty element) {
    ASTNode keyNode = element.getNode().findChildByType(BuckTypes.KEYWORDS);
    if (keyNode != null) {
      return keyNode.getText();
    } else {
      return null;
    }
  }

  public static String getValue(BuckProperty element) {
    ASTNode valueNode = element.getNode().findChildByType(BuckTypes.VALUE);
    if (valueNode != null) {
      return valueNode.getText();
    } else {
      return null;
    }
  }

  public static String getName(BuckProperty element) {
    return getKey(element);
  }

  public static PsiElement setName(BuckProperty element, String newName) {
    ASTNode keyNode = element.getNode().findChildByType(BuckTypes.KEYWORDS);
    if (keyNode != null) {

      BuckProperty property = BuckElementFactory.createProperty(element.getProject(), newName);
      ASTNode newKeyNode = property.getFirstChild().getNode();
      element.getNode().replaceChild(keyNode, newKeyNode);
    }
    return element;
  }

  public static PsiElement getNameIdentifier(BuckProperty element) {
    ASTNode keyNode = element.getNode().findChildByType(BuckTypes.KEYWORDS);
    if (keyNode != null) {
      return keyNode.getPsi();
    } else {
      return null;
    }
  }

}
