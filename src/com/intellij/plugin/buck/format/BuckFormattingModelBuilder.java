package com.intellij.plugin.buck.format;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuckFormattingModelBuilder implements FormattingModelBuilder {
  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    return FormattingModelProvider.createFormattingModelForPsiFile(element.getContainingFile(),
        new BuckBlock(
            element.getNode(),
            Wrap.createWrap(WrapType.NORMAL, false),
            Alignment.createAlignment(),
            createSpaceBuilder(settings)),
        settings);
  }

  private static SpacingBuilder createSpaceBuilder(CodeStyleSettings settings) {
      return new SpacingBuilder(settings, BuckLanguage.INSTANCE);
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    return null;
  }

}
