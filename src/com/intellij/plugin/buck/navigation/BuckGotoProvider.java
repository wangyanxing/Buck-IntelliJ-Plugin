package com.intellij.plugin.buck.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.build.BuckBuildTargetUtil;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

public class BuckGotoProvider extends GotoDeclarationHandlerBase {

  @Override
  public PsiElement getGotoDeclarationTarget(@Nullable PsiElement source, Editor editor) {
    if (source != null && source.getLanguage() instanceof BuckLanguage) {
      // The parent type of the element must be BuckValue
      BuckValue value = PsiTreeUtil.getParentOfType(source, BuckValue.class);
      if (value == null) {
        return null;
      }
      final Project project = editor.getProject();
      if (project == null) {
        return null;
      }

      String target = source.getText();
      if (target.startsWith("'") && target.endsWith("'")) {
        target = target.substring(1, target.length() - 1);
      }
      VirtualFile targetBuckFile =
          BuckBuildTargetUtil.getBuckFileFromAbsoluteTarget(project, target);
      if (targetBuckFile == null) {
        return null;
      }
      return PsiManager.getInstance(project).findFile(targetBuckFile);
    }
    return null;
  }
}
