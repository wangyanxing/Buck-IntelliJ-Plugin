package com.intellij.plugin.buck.build;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.file.BuckFileUtil;
import com.intellij.plugin.buck.lang.BuckFile;
import com.intellij.plugin.buck.lang.psi.BuckExpression;
import com.intellij.plugin.buck.lang.psi.BuckPsiUtils;
import com.intellij.plugin.buck.lang.psi.BuckRuleBody;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;

public class BuckBuildUtil {

  static public final String BUCK_CONFIG_FILE = ".buckconfig";
  static public final String BUCK_FILE_NAME = BuckFileUtil.getBuildFileName();
  static public final String PROJECT_CONFIG_RULE_NAME = "project_config";
  static public final String SRC_TARGET_PROPERTY_NAME = "src_target";

  public static boolean isValidAbsoluteTarget(String target) {
    return target.matches("^//[\\s\\S]*:[\\s\\S]*$");
  }

  /**
   * @param target The absolute target in "//apps/myapp:app" pattern
   * @return The absolute path of the target, for example "apps/myapp"
   */
  public static String extractAbsoluteTarget(String target) {
    return target.substring(2, target.lastIndexOf(":"));
  }

  /**
   * Return the virtual file of the BUCK file of the given target.
   */
  public static VirtualFile getBuckFileFromAbsoluteTarget(Project project, String target) {
    if (!isValidAbsoluteTarget(target)) {
      return null;
    }
    VirtualFile buckDir =
        project.getBaseDir().findFileByRelativePath(extractAbsoluteTarget(target));
    return buckDir != null ? buckDir.findChild(BUCK_FILE_NAME) : null;
  }

  /**
   * Find the buck file from a directory.
   */
  public static VirtualFile getBuckFileFromDirectory(VirtualFile file) {
    if (file == null) {
      return null;
    }
    VirtualFile buckFile = file.findChild(BUCK_FILE_NAME);
    while (buckFile == null && file != null) {
      buckFile = file.findChild(BUCK_FILE_NAME);
      file = file.getParent();
    }
    return buckFile;
  }

  /**
   * Get the buck target from a buck file.
   */
  public static String extractBuckTarget(Project project, VirtualFile file) {
    BuckFile buckFile = (BuckFile) PsiManager.getInstance(project).findFile(file);
    if (buckFile == null) {
      return null;
    }

    PsiElement[] children = buckFile.getChildren();
    for (PsiElement child : children) {
      if (child.getNode().getElementType() == BuckTypes.RULE_BLOCK) {
        PsiElement ruleName = child.getFirstChild();
        // Find rule "project_config"
        if (ruleName != null && BuckPsiUtils.testType(ruleName, BuckTypes.RULE_NAME)) {
          if (ruleName.getText().equals(PROJECT_CONFIG_RULE_NAME)) {
            // Find property "src_target"
            PsiElement bodyElement = BuckPsiUtils.findChildWithType(child, BuckTypes.RULE_BODY);
            return getPropertyValue((BuckRuleBody) bodyElement, SRC_TARGET_PROPERTY_NAME);
          }
        }
      }
    }
    return null;
  }

  /**
   * Get the full buck target string, for example: '//com/example/app:app'
   */
  public static String getFullBuckTarget(Project project, VirtualFile file) {
    String target = extractBuckTarget(project, file);
    if (target == null) {
      return null;
    }

    String path = file.getPath().replace(project.getBasePath(), "/");
    path = path.substring(0, path.lastIndexOf("/BUCK")) + target;
    return path;
  }

  /**
   * Get the value of a property in a specific buck rule body.
   */
  public static String getPropertyValue(BuckRuleBody body, String name) {
    if (body == null) {
      return null;
    }
    PsiElement[] children = body.getChildren();
    for (PsiElement child : children) {
      if (BuckPsiUtils.testType(child, BuckTypes.PROPERTY)) {
        PsiElement lvalue = child.getFirstChild();
        PsiElement propertyName = lvalue.getFirstChild();
        if (propertyName != null && propertyName.getText().equals(name)) {
          BuckExpression expression =
              (BuckExpression) BuckPsiUtils.findChildWithType(child, BuckTypes.EXPRESSION);
          return expression != null ? BuckPsiUtils.getStringValueFromExpression(expression) : null;
        }
      }
    }
    return null;
  }
}
