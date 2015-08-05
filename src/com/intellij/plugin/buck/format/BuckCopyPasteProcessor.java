package com.intellij.plugin.buck.format;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.plugin.buck.lang.BuckFile;
import com.intellij.plugin.buck.lang.psi.BuckPsiUtils;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuckCopyPasteProcessor implements CopyPastePreProcessor {

  private static final Pattern DEPENDENCY_PATTERN =
      Pattern.compile("^(package|import)\\s*([\\w\\.]*);?\\s*$");

  @Nullable
  @Override
  public String preprocessOnCopy(PsiFile psiFile, int[] ints, int[] ints1, String s) {
    return null;
  }

  @NotNull
  @Override
  public String preprocessOnPaste(
      Project project, PsiFile psiFile, Editor editor, String text, RawText rawText) {
    if (!(psiFile instanceof BuckFile)) {
      return text;
    }
    final Document document = editor.getDocument();
    PsiDocumentManager.getInstance(project).commitDocument(document);
    final SelectionModel selectionModel = editor.getSelectionModel();

    // Pastes in block selection mode (column mode) are not handled by a CopyPasteProcessor.
    final int selectionStart = selectionModel.getSelectionStart();
    final PsiElement element = psiFile.findElementAt(selectionStart);
    if (element == null) {
      return text;
    }

    if (BuckPsiUtils.hasElementType(
        element.getNode(), TokenType.WHITE_SPACE, BuckTypes.VALUE_STRING)) {
      PsiElement property = BuckPsiUtils.findAncestorWithType(element, BuckTypes.PROPERTY);
      if (checkPropertyName(property)) {
        text = buildBuckDependencyPath(element, project, text);
      }
    }
    return text;
  }

  protected boolean checkPropertyName(PsiElement property) {
    if (property == null) {
      return false;
    }
    PsiElement leftValue = property.getFirstChild();
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

  /**
   * Automatically convert to buck dependency pattern
   * Example 1:
   * "import com.example.activity.MyFirstActivity" -> "//java/com/example/activity:activity"
   *
   * Example 2:
   * "package com.example.activity;" -> "//java/com/example/activity:activity"
   *
   * Example 3:
   * "com.example.activity.MyFirstActivity" -> "//java/com/example/activity:activity"
   *
   * Example 4:
   * "/Users/tim/tb/java/com/example/activity/BUCK" -> "//java/com/example/activity:activity"
   */
  private String buildBuckDependencyPath(PsiElement element, Project project, String path) {
    String original = path;
    Matcher matcher = DEPENDENCY_PATTERN.matcher(path);
    if (matcher.matches()) {
      path = matcher.group(2);
    }

    VirtualFile buckFile = referenceNameToBuckFile(project, path);
    if (buckFile != null) {
      path = buckFile.getPath().replaceFirst(project.getBasePath(), "");
      path = "/" + path.replace('.', '/');
      path = path.substring(0, path.lastIndexOf("/"));

      String target = BuckBuildUtil.extractBuckTarget(project, buckFile);
      if (target != null) {
        path += target;
      } else {
        String lastWord = path.substring(path.lastIndexOf("/") + 1, path.length());
        path += ":" + lastWord;
      }
      if (element.getNode().getElementType() == TokenType.WHITE_SPACE) {
        path = "'" + path + "',";
      }
      return path;
    } else {
      return original;
    }
  }

  private VirtualFile referenceNameToBuckFile(Project project, String reference) {
    // First test if it is a absolute path of a file.
    File tryFile = new File(reference);
    if (tryFile != null) {
      VirtualFile file = VfsUtil.findFileByIoFile(tryFile, true);
      if (file != null) {
        return BuckBuildUtil.getBuckFileFromDirectory(file.getParent());
      }
    }

    // Try class firstly.
    PsiClass classElement = JavaPsiFacade.getInstance(project).findClass(
        reference, GlobalSearchScope.allScope(project));
    if (classElement != null) {
      VirtualFile file = PsiUtilCore.getVirtualFile(classElement);
      return BuckBuildUtil.getBuckFileFromDirectory(file.getParent());
    }

    // Then try package.
    PsiPackage packageElement = JavaPsiFacade.getInstance(project).findPackage(reference);
    if (packageElement != null) {
      PsiDirectory directory = packageElement.getDirectories()[0];
      return BuckBuildUtil.getBuckFileFromDirectory(directory.getVirtualFile());
    }

    // Extract the package from the reference.
    int index = reference.lastIndexOf(".");
    if (index == -1) {
      return null;
    }
    reference = reference.substring(0, index);

    // Try to find the package again.
    packageElement = JavaPsiFacade.getInstance(project).findPackage(reference);
    if (packageElement != null) {
      PsiDirectory directory = packageElement.getDirectories()[0];
      return BuckBuildUtil.getBuckFileFromDirectory(directory.getVirtualFile());
    }
    return null;
  }
}
