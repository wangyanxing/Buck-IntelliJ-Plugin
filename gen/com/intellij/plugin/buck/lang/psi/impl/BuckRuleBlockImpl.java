// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckRuleBlock;
import com.intellij.plugin.buck.lang.psi.BuckRuleBody;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class BuckRuleBlockImpl extends ASTWrapperPsiElement implements BuckRuleBlock {

  public BuckRuleBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor) visitor).visitRuleBlock(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public BuckRuleBody getRuleBody() {
    return findNotNullChildByClass(BuckRuleBody.class);
  }

}
