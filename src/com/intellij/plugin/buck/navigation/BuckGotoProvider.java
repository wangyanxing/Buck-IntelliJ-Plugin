package com.intellij.plugin.buck.navigation;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.plugin.buck.lang.psi.BuckValue;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BuckGotoProvider extends GotoDeclarationHandlerBase {

  /**
   * break a path down into individual elements and add to a list.
   * example : if a path is /a/b/c/d.txt, the breakdown will be [d.txt,c,b,a]
   *
   * @param f input file
   * @return a List collection with the individual elements of the path in
   * reverse order
   */
  private static List getPathList(File f) {
    List l = new ArrayList();
    File r;
    try {
      r = f.getCanonicalFile();
      while (r != null) {
        l.add(r.getName());
        r = r.getParentFile();
      }
    } catch (IOException e) {
      e.printStackTrace();
      l = null;
    }
    return l;
  }

  /**
   * figure out a string representing the relative path of
   * 'f' with respect to 'r'
   *
   * @param r home path
   * @param f path of file
   */
  private static String matchPathLists(List r, List f) {
    int i;
    int j;
    String s;
    // start at the beginning of the lists
    // iterate while both lists are equal
    s = "";
    i = r.size() - 1;
    j = f.size() - 1;

    // first eliminate common root
    while ((i >= 0) && (j >= 0) && (r.get(i).equals(f.get(j)))) {
      i--;
      j--;
    }

    // for each remaining level in the home path, add a ..
    for (; i >= 0; i--) {
      s += ".." + File.separator;
    }

    // for each level in the file path, add the path
    for (; j >= 1; j--) {
      s += f.get(j) + File.separator;
    }

    // file name
    s += f.get(j);
    return s;
  }

  /**
   * get relative path of File 'f' with respect to 'home' directory
   * example : home = /a/b/c
   * f    = /a/d/e/x.txt
   * s = getRelativePath(home,f) = ../../d/e/x.txt
   *
   * @param home base path, should be a directory, not a file, or it doesn't
   *             make sense
   * @param f    file to generate path for
   * @return path from home to f as a string
   */
  public static String getRelativePath(File home, File f) {
    File r;
    List homelist;
    List filelist;
    String s;

    homelist = getPathList(home);
    filelist = getPathList(f);
    s = matchPathLists(homelist, filelist);

    return s;
  }

  @Override
  public PsiElement getGotoDeclarationTarget(@Nullable PsiElement source, Editor editor) {
    if (source != null && source.getLanguage() instanceof BuckLanguage) {
      BuckValue ref = PsiTreeUtil.getParentOfType(source, BuckValue.class);
      if (ref == null) {
        return null;
      }

      String target = source.getText();
      if (!target.matches("^'\\/\\/[\\s\\S]*:[\\s\\S]*'$")) {
        return null;
      }
      target = target.substring(3, target.lastIndexOf(":"));

      final Project project = editor.getProject();
      if (project == null) {
        return null;
      }
      final Document document = editor.getDocument();
      if (document == null) {
        return null;
      }
      VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
      if (virtualFile == null) {
        return null;
      }

      String projectPath = project.getBasePath();
      String filePath = virtualFile.getPath();

      target = projectPath + "/" + target + "/BUCK";
      //String relative = new File(filePath).toURI().relativize(new File(target).toURI()).getPath();
      String relative = getRelativePath(new File(filePath), new File(target));

      VirtualFile targetBuckFile = virtualFile.findFileByRelativePath(relative);
      if (targetBuckFile == null) {
        return null;
      }

      return PsiManager.getInstance(project).findFile(targetBuckFile);
    }
    return null;
  }


}

