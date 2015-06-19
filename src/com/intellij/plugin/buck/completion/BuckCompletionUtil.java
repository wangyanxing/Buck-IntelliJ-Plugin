package com.intellij.plugin.buck.completion;

import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiSubstitutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BuckCompletionUtil {
    private static final SingleCharInsertHandler QUOTES_INSERT_HANDLER = new SingleCharInsertHandler('(');

    @NotNull
    public static LookupElement createQuotesLookupElement() {
        return LookupElementBuilder.create("(")
                .withInsertHandler(QUOTES_INSERT_HANDLER);
    }

    public static LookupElement createLookupElement() {
        LookupElementBuilder builder = LookupElementBuilder.create("(");
        return builder.withInsertHandler(QUOTES_INSERT_HANDLER);
    }

    public static LookupElement createLookupElement(PsiNamedElement o) {
        return setupLookupBuilder(o, PsiSubstitutor.EMPTY, LookupElementBuilder.create(o, o.getName()), null);
    }

    private static LookupElementBuilder setupLookupBuilder(PsiElement element,
                                                           PsiSubstitutor substitutor,
                                                           LookupElementBuilder builder,
                                                           @Nullable PsiElement position) {
        builder = builder.withIcon(element.getIcon(Iconable.ICON_FLAG_VISIBILITY | Iconable.ICON_FLAG_READ_STATUS))
                .withInsertHandler(QUOTES_INSERT_HANDLER);
        //builder = setTailText(element, builder, substitutor);
        //builder = setTypeText(element, builder, substitutor, position);
        return builder;
    }
}
