package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;

public abstract class BuckNamedElementImpl
    extends ASTWrapperPsiElement implements PsiNameIdentifierOwner {

  public BuckNamedElementImpl(@NotNull ASTNode node) {
    super(node);
  }
}
