package com.intellij.plugin.buck.format;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.plugin.buck.lang.psi.BuckPsiUtils;

public class BuckQuoteHandler extends SimpleTokenSetQuoteHandler {

  public BuckQuoteHandler() {
    super(BuckPsiUtils.STRING_LITERALS);
  }
}
