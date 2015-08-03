package com.intellij.plugin.buck.build;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.lang.BuckFile;
import com.intellij.plugin.buck.lang.psi.BuckPsiUtils;
import com.intellij.plugin.buck.lang.psi.BuckRuleBody;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiUtil;

public class BuckBuildUtil {

  static public String BUCK_CONFIG_FILE = ".buckconfig";
  static public String BUCK_FILE_NAME = "BUCK";

  static private String PROJECT_CONFIG_RULE_NAME = "project_config";
  static private String SRC_TARGET_PROPERTY_NAME = "src_target";

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
   * Return the virtual file of the BUCK file of the given target
   */
  public static VirtualFile getBuckFileFromAbsoluteTarget(Project project, String target) {
    if (!isValidAbsoluteTarget(target)) {
      return null;
    }
    VirtualFile buckDir =
        project.getBaseDir().findFileByRelativePath(extractAbsoluteTarget(target));
    return buckDir != null ? buckDir.findChild(BUCK_FILE_NAME) : null;
  }

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
   * Get the buck target from a buck file
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
        if (ruleName != null &&
            ruleName.getNode().getElementType() == BuckTypes.RULE_NAMES &&
            ruleName.getText().equals(PROJECT_CONFIG_RULE_NAME)) {

          // Find property "src_target"
          PsiElement bodyElement = BuckPsiUtils.findChildWithType(child, BuckTypes.RULE_BODY);
          return getPropertyValue((BuckRuleBody) bodyElement, SRC_TARGET_PROPERTY_NAME);
        }
      }
    }
    return null;
  }


  /**
   * Get the value of a property in a specific buck rule body
   */
  public static String getPropertyValue(BuckRuleBody body, String name) {
    if (body == null) {
      return null;
    }
    PsiElement[] children = body.getChildren();
    for (PsiElement child : children) {
      if (child.getNode().getElementType() == BuckTypes.PROPERTY) {
        PsiElement propertyName = child.getFirstChild();
        if (propertyName != null && propertyName.getText().equals(name)) {
          PsiElement value = BuckPsiUtils.findChildWithType(child, BuckTypes.VALUE);
          if (value != null && value.getFirstChild() != null &&
              value.getFirstChild().getNode().getElementType() == BuckTypes.VALUE_STRING) {
            String text = value.getText();
            return text.length() > 2 ? text.substring(1, text.length() - 1) : null;
          }
        }
      }
    }
    return null;
  }
}
