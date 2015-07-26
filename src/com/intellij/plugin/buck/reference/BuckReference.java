package com.intellij.plugin.buck.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BuckReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
  private String key;

  public BuckReference(@NotNull PsiElement element, TextRange textRange) {
    super(element, textRange);
    key = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
  }

  @NotNull
  @Override
  public ResolveResult[] multiResolve(boolean incompleteCode) {
    Project project = myElement.getProject();
    //final List<BuckProperty> properties = BuckPsiUtils.findProperties(project, key);
    List<ResolveResult> results = new ArrayList<ResolveResult>();
//    for (BuckProperty property : properties) {
//      results.add(new PsiElementResolveResult(property));
//    }
    return results.toArray(new ResolveResult[results.size()]);
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    ResolveResult[] resolveResults = multiResolve(false);
    return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
  }

  @NotNull
  @Override
  public Object[] getVariants() {
    Project project = myElement.getProject();
    //List<BuckProperty> properties = BuckUtil.findProperties(project);
    List<LookupElement> variants = new ArrayList<LookupElement>();
//    for (final BuckProperty property : properties) {
//      if (property.getKey() != null && property.getKey().length() > 0) {
//        variants.add(LookupElementBuilder.create(property).
//                withIcon(BuckIcons.FILE_TYPE).
//                withTypeText(property.getContainingFile().getName())
//        );
//      }
//    }
    return variants.toArray();
  }
}