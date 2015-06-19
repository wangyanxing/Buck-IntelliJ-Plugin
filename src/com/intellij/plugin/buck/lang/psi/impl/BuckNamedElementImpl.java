package com.intellij.plugin.buck.lang.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.plugin.buck.lang.psi.BuckNamedElement;
import org.jetbrains.annotations.NotNull;

public abstract class BuckNamedElementImpl extends ASTWrapperPsiElement implements BuckNamedElement {
    public BuckNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }
}
