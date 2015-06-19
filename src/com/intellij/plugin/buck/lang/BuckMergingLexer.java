package com.intellij.plugin.buck.lang;

import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.TokenSet;

public class BuckMergingLexer extends MergingLexerAdapter {
    private static final TokenSet tokensToMerge = TokenSet.create(TokenType.WHITE_SPACE);

    public BuckMergingLexer(){
        super(new BuckLexerAdapter(),tokensToMerge);
    }
}