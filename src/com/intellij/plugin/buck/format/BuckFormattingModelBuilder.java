package com.intellij.plugin.buck.format;

import com.intellij.formatting.CustomFormattingModelBuilder;
import com.intellij.formatting.FormatTextRanges;
import com.intellij.formatting.FormattingMode;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilderEx;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Indent;
import com.intellij.formatting.SpacingBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implements the code formatting for BUCK files
 */
public class BuckFormattingModelBuilder implements
    FormattingModelBuilderEx, CustomFormattingModelBuilder {

  @NotNull
  @Override
  public FormattingModel createModel(@NotNull PsiElement element,
                                     @NotNull CodeStyleSettings settings,
                                     @NotNull FormattingMode mode) {
    final BuckBlock block =
        new BuckBlock(null, element.getNode(), settings, null, Indent.getNoneIndent(), null);
    return FormattingModelProvider.createFormattingModelForPsiFile(
        element.getContainingFile(),
        block,
        settings);
  }

  @Nullable
  @Override
  public CommonCodeStyleSettings.IndentOptions getIndentOptionsToUse(
      @NotNull PsiFile file,
      @NotNull FormatTextRanges ranges,
      @NotNull CodeStyleSettings settings) {
    return null;
  }

  @NotNull
  @Override
  public FormattingModel createModel(PsiElement element, CodeStyleSettings settings) {
    return createModel(element, settings, FormattingMode.REFORMAT);
  }

  @Nullable
  @Override
  public TextRange getRangeAffectingIndent(PsiFile file, int offset, ASTNode elementAtOffset) {
    final PsiElement element = elementAtOffset.getPsi();
    final PsiElement container = element.getParent();
    return container != null ? container.getTextRange() : null;
  }

  @Override
  public boolean isEngagedToFormat(PsiElement context) {
    PsiFile file = context.getContainingFile();
    return file != null && file.getLanguage() == BuckLanguage.INSTANCE;
  }

  protected static SpacingBuilder createSpacingBuilder(CodeStyleSettings settings) {
    return new SpacingBuilder(settings, BuckLanguage.INSTANCE)
        .between(BuckTypes.RULE_BLOCK, BuckTypes.RULE_BLOCK).blankLines(1)
        .before(BuckTypes.L_BRACKET).spacing(0, 0, 0, false, 0)
        .before(BuckTypes.L_PARENTHESES).spacing(0, 0, 0, false, 0)
        .before(BuckTypes.EQUAL).spacing(1, 1, 0, false, 0)
        .after(BuckTypes.EQUAL).spacing(1, 1, 0, false, 0)
        .before(BuckTypes.COMMA).spacing(0, 0, 0, false, 0)
        .after(BuckTypes.COMMA).lineBreakInCode()
        .before(BuckTypes.R_PARENTHESES).lineBreakInCode()
        .before(BuckTypes.R_BRACKET).lineBreakInCode()
        .before(BuckTypes.RULE_BLOCK).blankLines(1);
  }
}