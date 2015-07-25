package com.intellij.plugin.buck.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.plugin.buck.targets.BuckTargetUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Annotator for Buck, it helps highlight and annotate any issue in Buck files
 */
public class BuckAnnotator implements Annotator {

  private static final String ANNOTATOR_ERROR_CANNOT_LOCATE_TARGET = "Cannot locate the Buck target";

  @Override
  public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
    BuckValue value = PsiTreeUtil.getParentOfType(psiElement, BuckValue.class);
    if (value == null) {
      return;
    }
    final Project project = psiElement.getProject();
    if (project == null) {
      return;
    }

    String target = psiElement.getText();
    if (target.startsWith("'") && target.endsWith("'")) {
      target = target.substring(1, target.length() - 1);
    } else {
      return;
    }
    if (!BuckTargetUtil.isValidAbsoluteTarget(target)) {
      return;
    }
    VirtualFile buckDir =
        project.getBaseDir().findFileByRelativePath(BuckTargetUtil.extractAbsoluteTarget(target));
    VirtualFile targetBuckFile = buckDir != null ? buckDir.findChild("BUCK") : null;

    // Show error annotate if can't find this buck target
    if (targetBuckFile == null) {
      TextRange range = new TextRange(psiElement.getTextRange().getStartOffset(),
          psiElement.getTextRange().getEndOffset());
      annotationHolder.createErrorAnnotation(range, ANNOTATOR_ERROR_CANNOT_LOCATE_TARGET);
    }
  }
}
