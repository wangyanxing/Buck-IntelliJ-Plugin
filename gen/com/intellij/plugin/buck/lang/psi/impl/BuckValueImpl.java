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

public class BuckValueImpl extends ASTWrapperPsiElement implements BuckValue {

  public BuckValueImpl(ASTNode node) {
    super(node);
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof BuckVisitor) ((BuckVisitor)visitor).visitValue(this);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public BuckGlobBlock getGlobBlock() {
    return findChildByClass(BuckGlobBlock.class);
  }

  @Override
  @Nullable
  public BuckList getList() {
    return findChildByClass(BuckList.class);
  }

  @Override
  @Nullable
  public BuckObject getObject() {
    return findChildByClass(BuckObject.class);
  }

  @Override
  @Nullable
  public BuckValueArray getValueArray() {
    return findChildByClass(BuckValueArray.class);
  }

  @Override
  @Nullable
  public PsiElement getBoolean() {
    return findChildByType(BOOLEAN);
  }

  @Override
  @Nullable
  public PsiElement getDoubleQuotedString() {
    return findChildByType(DOUBLE_QUOTED_STRING);
  }

  @Override
  @Nullable
  public PsiElement getMacros() {
    return findChildByType(MACROS);
  }

  @Override
  @Nullable
  public PsiElement getNumber() {
    return findChildByType(NUMBER);
  }

  @Override
  @Nullable
  public PsiElement getSingleQuotedString() {
    return findChildByType(SINGLE_QUOTED_STRING);
  }

}
