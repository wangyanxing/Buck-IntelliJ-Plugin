// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface BuckValue extends PsiElement {

  @Nullable
  BuckList getList();

  @Nullable
  BuckRuleBlock getRuleBlock();

  @Nullable
  BuckValueArray getValueArray();

}
