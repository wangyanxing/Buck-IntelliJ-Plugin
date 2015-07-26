// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckRuleBlock;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.plugin.buck.lang.psi.BuckValueArray;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuckValueImpl extends ASTWrapperPsiElement implements BuckValue {

  public BuckValueImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor) visitor).visitValue(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BuckRuleBlock getRuleBlock() {
    return findChildByClass(BuckRuleBlock.class);
  }

  @Override
  @Nullable
  public BuckValueArray getValueArray() {
    return findChildByClass(BuckValueArray.class);
  }

}
