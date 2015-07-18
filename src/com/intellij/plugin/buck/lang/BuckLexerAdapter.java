package com.intellij.plugin.buck.lang;

import com.intellij.lexer.FlexAdapter;

import java.io.Reader;

public class BuckLexerAdapter extends FlexAdapter {
  public BuckLexerAdapter() {
    super(new _BuckLexer((Reader) null));
  }
}
