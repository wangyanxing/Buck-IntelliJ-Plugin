// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckProperty;
import com.intellij.plugin.buck.lang.psi.BuckPropertyLvalue;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuckPropertyImpl extends BuckNamedElementImpl implements BuckProperty {

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

  @Nullable
  @Override
  public PsiElement getNameIdentifier() {
    return null;
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String s) throws IncorrectOperationException {
    return null;
  }
}
