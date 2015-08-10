package com.intellij.plugin.buck.format;

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

public class BuckBraceMatcher implements PairedBraceMatcher {

  private static BracePair[] PAIRS = {
      new BracePair(BuckTypes.L_BRACKET, BuckTypes.R_BRACKET, true),
      new BracePair(BuckTypes.L_PARENTHESES, BuckTypes.R_PARENTHESES, true),
      new BracePair(BuckTypes.L_CURLY, BuckTypes.R_CURLY, true),
  };

  @Override
  public BracePair[] getPairs() {
    return PAIRS;
  }

  @Override
  public boolean isPairedBracesAllowedBeforeType(
      IElementType lbraceType, IElementType contextType) {
    return true;
  }

  @Override
  public int getCodeConstructStart(PsiFile psiFile, int openingBraceOffset) {
    return openingBraceOffset;
  }
}
