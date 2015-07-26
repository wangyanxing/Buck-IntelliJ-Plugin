// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckProperty;
import com.intellij.plugin.buck.lang.psi.BuckPropertyLvalue;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuckPropertyImpl extends ASTWrapperPsiElement implements BuckProperty {

  public BuckPropertyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor) visitor).visitProperty(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BuckPropertyLvalue getPropertyLvalue() {
    return findChildByClass(BuckPropertyLvalue.class);
  }

  @Override
  @NotNull
  public BuckValue getValue() {
    return findNotNullChildByClass(BuckValue.class);
  }

}
