// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BuckValue extends PsiElement {

  @Nullable
  BuckGlobBlock getGlobBlock();

  @Nullable
  BuckList getList();

  @Nullable
  BuckValueArray getValueArray();

  @Nullable
  PsiElement getBoolean();

  @Nullable
  PsiElement getDoubleQuotedString();

  @Nullable
  PsiElement getMacros();

  @Nullable
  PsiElement getNumber();

  @Nullable
  PsiElement getSingleQuotedString();

}
