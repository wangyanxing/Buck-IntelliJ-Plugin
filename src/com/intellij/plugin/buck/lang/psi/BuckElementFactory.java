package com.intellij.plugin.buck.lang.psi;


import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.file.BuckFileType;
import com.intellij.plugin.buck.lang.BuckFile;
import com.intellij.psi.PsiFileFactory;

public class BuckElementFactory {
  public static BuckProperty createProperty(Project project, String name) {
    final BuckFile file = createFile(project, name);
    return (BuckProperty) file.getFirstChild();
  }

  public static BuckFile createFile(Project project, String text) {
    String name = "dummy.simple";
    return (BuckFile) PsiFileFactory.getInstance(project).
        createFileFromText(name, BuckFileType.INSTANCE, text);
  }
}