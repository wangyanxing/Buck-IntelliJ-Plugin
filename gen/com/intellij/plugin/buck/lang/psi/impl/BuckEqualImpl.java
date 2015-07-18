// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckEqual;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class BuckEqualImpl extends ASTWrapperPsiElement implements BuckEqual {

  public BuckEqualImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor) visitor).visitEqual(this);
    else super.accept(visitor);
  }

}
