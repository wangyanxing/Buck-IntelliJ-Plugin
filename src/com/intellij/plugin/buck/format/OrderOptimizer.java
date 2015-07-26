package com.intellij.plugin.buck.format;

import com.intellij.plugin.buck.lang.psi.*;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Comparator;

public class OrderOptimizer {

  private static String DEPS_KEYWORD = "deps";

  public static void optimzeDeps(@NotNull PsiFile file) {
    final PropertyVisitor visitor = new PropertyVisitor();
    file.accept(new BuckVisitor() {
      @Override
      public void visitElement(PsiElement node) {
        node.acceptChildren(this);
        node.accept(visitor);
      }
    });

    // Commit modifications
    final PsiDocumentManager manager = PsiDocumentManager.getInstance(file.getProject());
    manager.doPostponedOperationsAndUnblockDocument(manager.getDocument(file));
  }

  private static class PropertyVisitor extends BuckVisitor {
    @Override
    public void visitProperty(@NotNull BuckProperty property) {
      BuckPropertyLvalue lValue = property.getPropertyLvalue();
      if (lValue == null || !lValue.getText().equals(DEPS_KEYWORD)) {
        return;
      }
      BuckValueArray array = property.getValue().getValueArray();
      if (array == null) {
        return;
      }

      BuckArrayElements arrayElements = array.getArrayElements();
      PsiElement[] arrayValues = arrayElements.getChildren();
      Arrays.sort(arrayValues, new Comparator<PsiElement>() {
            @Override
            public int compare(PsiElement e1, PsiElement e2) {
              return e1.getText().compareTo(e2.getText());
            }
          }
      );
      PsiElement[] oldValues = new PsiElement[arrayValues.length];
      for (int i = 0; i < arrayValues.length; ++i) {
        oldValues[i] = arrayValues[i].copy();
      }

      for (int i = 0; i < arrayValues.length; ++i) {
        arrayElements.getChildren()[i].replace(oldValues[i]);
      }
    }
  }
}
