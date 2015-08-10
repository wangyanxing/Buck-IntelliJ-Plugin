package com.intellij.plugin.buck.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.plugin.buck.file.BuckFileUtil;
import com.intellij.plugin.buck.highlight.BuckSyntaxHighlighter;
import com.intellij.plugin.buck.lang.psi.BuckPsiUtils;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Annotator for Buck, it helps highlight and annotate any issue in Buck files
 */
public class BuckAnnotator implements Annotator {

  private static final String ANNOTATOR_ERROR_CANNOT_LOCATE_TARGET =
      "Cannot locate the Buck target";

  @Override
  public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
    if (annotateIdentifier(psiElement, annotationHolder)) {
      return;
    }
    annotateErrors(psiElement, annotationHolder);
  }

  private void annotateErrors(PsiElement psiElement, AnnotationHolder annotationHolder) {
    BuckValue value = PsiTreeUtil.getParentOfType(psiElement, BuckValue.class);
    if (value == null) {
      return;
    }
    final Project project = psiElement.getProject();
    if (project == null) {
      return;
    }

    String target = psiElement.getText();
    if (target.matches("\".*\"") || target.matches("'.*'")) {
      target = target.substring(1, target.length() - 1);
    } else {
      return;
    }
    if (!BuckBuildUtil.isValidAbsoluteTarget(target)) {
      return;
    }
    VirtualFile buckDir = project.getBaseDir().findFileByRelativePath(
        BuckBuildUtil.extractAbsoluteTarget(target));
    VirtualFile targetBuckFile =
        buckDir != null ? buckDir.findChild(BuckFileUtil.getBuildFileName()) : null;

    if (targetBuckFile == null) {
      TextRange range = new TextRange(psiElement.getTextRange().getStartOffset(),
          psiElement.getTextRange().getEndOffset());
      annotationHolder.createErrorAnnotation(range, ANNOTATOR_ERROR_CANNOT_LOCATE_TARGET);
    }
  }

  private boolean annotateIdentifier(PsiElement psiElement, AnnotationHolder annotationHolder) {
    if (psiElement.getNode().getElementType() != BuckTypes.IDENTIFIER) {
      return false;
    }

    PsiElement parent = psiElement.getParent();
    assert parent != null;

    final Annotation annotation = annotationHolder.createInfoAnnotation(psiElement, null);
    if (BuckPsiUtils.testType(parent, BuckTypes.RULE_NAME)) {
      annotation.setTextAttributes(BuckSyntaxHighlighter.BUCK_RULE_NAME);
      return true;
    } else if (BuckPsiUtils.testType(parent, BuckTypes.PROPERTY_LVALUE)) {
      annotation.setTextAttributes(BuckSyntaxHighlighter.BUCK_KEYWORD);
      return true;
    }
    return false;
  }
}
