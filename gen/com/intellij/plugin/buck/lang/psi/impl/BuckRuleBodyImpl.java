// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckProperty;
import com.intellij.plugin.buck.lang.psi.BuckRuleBody;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuckRuleBodyImpl extends ASTWrapperPsiElement implements BuckRuleBody {

  public BuckRuleBodyImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor) visitor).visitRuleBody(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BuckProperty> getPropertyList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BuckProperty.class);
  }

}
