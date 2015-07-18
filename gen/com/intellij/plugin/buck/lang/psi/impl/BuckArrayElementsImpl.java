// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckArrayElements;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.plugin.buck.lang.psi.BuckVisitor;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BuckArrayElementsImpl extends ASTWrapperPsiElement implements BuckArrayElements {

  public BuckArrayElementsImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor) visitor).visitArrayElements(this);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<BuckValue> getValueList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, BuckValue.class);
  }

}
