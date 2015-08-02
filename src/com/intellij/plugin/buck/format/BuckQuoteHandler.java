package com.intellij.plugin.buck.format;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.plugin.buck.lang.psi.BuckTypes;

public class BuckQuoteHandler extends SimpleTokenSetQuoteHandler {

  public BuckQuoteHandler() {
    super(BuckTypes.VALUE_STRING);
  }
}
