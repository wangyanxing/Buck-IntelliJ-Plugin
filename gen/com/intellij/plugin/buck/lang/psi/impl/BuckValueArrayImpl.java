// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckArrayElements;
import com.intellij.plugin.buck.lang.psi.BuckValueArray;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class BuckValueArrayImpl extends ASTWrapperPsiElement implements BuckValueArray {

  public BuckValueArrayImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor) visitor).visitValueArray(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public BuckArrayElements getArrayElements() {
    return findNotNullChildByClass(BuckArrayElements.class);
  }

}
