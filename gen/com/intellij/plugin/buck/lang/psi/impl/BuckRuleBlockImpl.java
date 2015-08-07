// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.intellij.plugin.buck.lang.psi.BuckTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.plugin.buck.lang.psi.*;

public class BuckRuleBlockImpl extends ASTWrapperPsiElement implements BuckRuleBlock {

  public BuckRuleBlockImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor)visitor).visitRuleBlock(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public BuckRuleBody getRuleBody() {
    return findNotNullChildByClass(BuckRuleBody.class);
  }

  @Override
  @NotNull
  public BuckRuleName getRuleName() {
    return findNotNullChildByClass(BuckRuleName.class);
  }

}
