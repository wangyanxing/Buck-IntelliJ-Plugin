package com.intellij.plugin.buck.format;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuckCopyPasteProcessor implements CopyPastePreProcessor {

  @Nullable
  @Override
  public String preprocessOnCopy(PsiFile psiFile, int[] ints, int[] ints1, String s) {
    return null;
  }

  @NotNull
  @Override
  public String preprocessOnPaste(
      Project project, PsiFile psiFile, Editor editor, String text, RawText rawText) {
    final Document document = editor.getDocument();
    PsiDocumentManager.getInstance(project).commitDocument(document);
    final SelectionModel selectionModel = editor.getSelectionModel();

    // pastes in block selection mode (column mode) are not handled by a CopyPasteProcessor
    final int selectionStart = selectionModel.getSelectionStart();
    final int selectionEnd = selectionModel.getSelectionEnd();
    PsiElement token = findLiteralTokenType(psiFile, selectionStart, selectionEnd);
    if (token == null) {
      return text;
    }

    if (isStringLiteral(token)) {
      StringBuilder buffer = new StringBuilder(text.length());
      @NonNls String breaker = getLineBreaker(token);
      final String[] lines = LineTokenizer.tokenize(text.toCharArray(), false, true);
      for (int i = 0; i < lines.length; i++) {
        buffer.append(escapeCharCharacters(lines[i], token));
        if (i != lines.length - 1) {
          buffer.append(breaker);
        }
        else if (text.endsWith("\n")) {
          buffer.append("\\n");
        }
      }
      text = buffer.toString();
    }

    return text;
  }

  @Nullable
  protected PsiElement findLiteralTokenType(PsiFile file, int selectionStart, int selectionEnd) {
    final PsiElement elementAtSelectionStart = file.findElementAt(selectionStart);
    if (elementAtSelectionStart == null) {
      return null;
    }

    if (!isStringLiteral(elementAtSelectionStart)) {
      return null;
    }

    if (elementAtSelectionStart.getTextRange().getEndOffset() < selectionEnd) {
      final PsiElement elementAtSelectionEnd = file.findElementAt(selectionEnd);
      if (elementAtSelectionEnd == null) {
        return null;
      }
      if (elementAtSelectionEnd.getNode().getElementType() ==
          elementAtSelectionStart.getNode().getElementType() &&
          elementAtSelectionEnd.getTextRange().getStartOffset() < selectionEnd) {
        return elementAtSelectionStart;
      }
    }

    final TextRange textRange = elementAtSelectionStart.getTextRange();
    if (selectionStart <= textRange.getStartOffset() || selectionEnd >= textRange.getEndOffset()) {
      return null;
    }
    return elementAtSelectionStart;
  }

  protected String getLineBreaker(PsiElement token) {
    CommonCodeStyleSettings codeStyleSettings =
        CodeStyleSettingsManager.getSettings(
            token.getProject()).getCommonSettings(token.getLanguage());
    return codeStyleSettings.BINARY_OPERATION_SIGN_ON_NEXT_LINE ? "\\n\"\n+ \"" : "\\n\" +\n\"";
  }

  protected boolean isStringLiteral(@NotNull PsiElement token) {
    ASTNode node = token.getNode();
    return node.getElementType() == BuckTypes.VALUE_STRING;
  }

  protected boolean checkToken(@NotNull PsiElement token) {
    PsiElement current = token.getParent();
    while (current != null) {
      if (current.getNode().getElementType() == BuckTypes.PROPERTY) {
        break;
      }
      current = current.getParent();
    }
    if (current == null) {
      return false;
    }
    PsiElement leftValue = current.getFirstChild();
    if (leftValue == null || leftValue.getNode().getElementType() != BuckTypes.PROPERTY_LVALUE) {
      return false;
    }
    leftValue = leftValue.getFirstChild();
    if (leftValue == null || leftValue.getNode().getElementType() != BuckTypes.KEYWORDS) {
      return false;
    }
    if (leftValue.getText().equals("deps") ||
        leftValue.getText().equals("visibility")) {
      return true;
    }
    return false;
  }

  @NotNull
  protected String escapeCharCharacters(@NotNull String s, @NotNull PsiElement token) {
    if (!checkToken(token)) {
      return s;
    }
    // TODO "com.example.activity" -> "com/example/activity"
    return s;
  }
}
