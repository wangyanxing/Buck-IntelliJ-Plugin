package com.intellij.plugin.buck.format;


import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class BuckFormatUtil {
  public static boolean hasElementType(@NotNull ASTNode node, @NotNull TokenSet set) {
    return set.contains(node.getElementType());
  }

  public static boolean hasElementType(@NotNull ASTNode node, IElementType... types) {
    return hasElementType(node, TokenSet.create(types));
  }

  public static boolean hasElementType(@NotNull PsiElement element, @NotNull TokenSet set) {
    return element.getNode() != null && hasElementType(element.getNode(), set);
  }

  public static boolean hasElementType(@NotNull PsiElement element, IElementType... types) {
    return element.getNode() != null && hasElementType(element.getNode(), types);
  }
}
