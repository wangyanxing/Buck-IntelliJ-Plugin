// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BuckProperty extends PsiElement {

  @Nullable
  BuckPropertyLvalue getPropertyLvalue();

  @NotNull
  BuckValue getValue();

}
