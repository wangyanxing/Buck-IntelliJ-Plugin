package com.intellij.plugin.buck.format;

import com.intellij.lang.ImportOptimizer;
import com.intellij.plugin.buck.lang.BuckFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Import optimizer is used for sort and remove unused java/python imports
 * Buck has no imports, but we use it to sort dependencies.
 */
public class BuckImportOptimizer implements ImportOptimizer {
  @Override
  public boolean supports(PsiFile psiFile) {
    return psiFile instanceof BuckFile;
  }

  @NotNull
  @Override
  public Runnable processFile(final PsiFile psiFile) {
    return new Runnable() {
      @Override
      public void run() {
        DependenciesOptimizer.optimzeDeps(psiFile);
      }
    };
  }
}
