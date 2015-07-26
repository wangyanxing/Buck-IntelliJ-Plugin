// This is a generated file. Not intended for manual editing.
package com.intellij.plugin.buck.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

public class BuckVisitor extends PsiElementVisitor {

  public void visitArrayElements(@NotNull BuckArrayElements o) {
    visitPsiElement(o);
  }

  public void visitComma(@NotNull BuckComma o) {
    visitPsiElement(o);
  }

  public void visitEqual(@NotNull BuckEqual o) {
    visitPsiElement(o);
  }

  public void visitLbrace(@NotNull BuckLbrace o) {
    visitPsiElement(o);
  }

  public void visitProperty(@NotNull BuckProperty o) {
    visitNamedElement(o);
  }

  public void visitPropertyLvalue(@NotNull BuckPropertyLvalue o) {
    visitPsiElement(o);
  }

  public void visitRbrace(@NotNull BuckRbrace o) {
    visitPsiElement(o);
  }

  public void visitRuleBlock(@NotNull BuckRuleBlock o) {
    visitPsiElement(o);
  }

  public void visitRuleBody(@NotNull BuckRuleBody o) {
    visitPsiElement(o);
  }

  public void visitSemicolon(@NotNull BuckSemicolon o) {
    visitPsiElement(o);
  }

  public void visitValue(@NotNull BuckValue o) {
    visitPsiElement(o);
  }

  public void visitValueArray(@NotNull BuckValueArray o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull BuckNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
