package com.intellij.plugin.buck.lang.psi;

import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class BuckTokenType extends IElementType {
  public BuckTokenType(@NotNull @NonNls String debugName) {
    super(debugName, BuckLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "BuckTokenType." + super.toString();
  }
}
