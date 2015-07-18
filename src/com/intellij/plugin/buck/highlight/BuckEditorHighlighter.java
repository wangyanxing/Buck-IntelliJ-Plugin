package com.intellij.plugin.buck.highlight;

import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LexerEditorHighlighter;

public class BuckEditorHighlighter extends LexerEditorHighlighter {
  public BuckEditorHighlighter(EditorColorsScheme scheme) {
    super(new BuckSyntaxHighlighter(), scheme);
  }
}
