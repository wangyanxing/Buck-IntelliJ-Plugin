package com.intellij.plugin.buck.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Auto-completion for keywords and rule names
 */
public class BuckCompletionContributor extends CompletionContributor {

  public static final ArrayList<String> sKeywords = new ArrayList<String>() {{
    add("name");
    add("res");
    add("binary_jar");
    add("srcs");
    add("deps");
    add("manifest");
    add("package_type");
    add("glob");
    add("visibility");
    add("aar");
    add("src_target");
    add("src_roots");
    add("java7_support");
    add("source_under_test");
    add("test_library_project_dir");
    add("contacts");
    add("exported_deps");
    add("excludes");
    add("main");
    add("resources");
    add("javadoc_url");
    add("store");
    add("properties");
    add("assets");
    add("package");
    add("proguard_config");
    add("source_jar");
    add("aidl");
    add("import_path");
    add("annotation_processors");
    add("annotation_processor_deps");
    add("keystore");
  }};

  public static final ArrayList<String> sRuleNames = new ArrayList<String>() {{
    add("genrule");
    add("remote_file");
    add("android_aar");
    add("android_binary");
    add("android_build_config");
    add("android_library");
    add("android_manifest");
    add("android_prebuilt_aar");
    add("android_resource");
    add("apk_genrule");
    add("cxx_library");
    add("gen_aidl");
    add("ndk_library");
    add("prebuilt_jar");
    add("prebuilt_native_library");
    add("project_config");
    add("cxx_binary");
    add("cxx_library");
    add("cxx_test");
    add("prebuilt_native_library");
    add("d_binary");
    add("d_library");
    add("d_test");
    add("cxx_library");
    add("java_binary");
    add("java_library");
    add("java_test");
    add("prebuilt_jar");
    add("prebuilt_native_library");
    add("prebuilt_python_library");
    add("python_binary");
    add("python_library");
    add("python_test");
    add("glob");
    add("include_defs");
    add("robolectric_test");
    add("keystore");
  }};

  public BuckCompletionContributor() {
    // Auto completion for basic rule names
    extend(
        CompletionType.BASIC,
        PlatformPatterns.psiElement(BuckTypes.IDENTIFIER).withLanguage(BuckLanguage.INSTANCE),
        BuckKeywordsCompletionProvider.INSTANCE);
  }

  private static class BuckKeywordsCompletionProvider extends CompletionProvider<CompletionParameters> {
    private static final BuckKeywordsCompletionProvider INSTANCE = new BuckKeywordsCompletionProvider();

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
      for (String card : sKeywords) {
        result.addElement(LookupElementBuilder.create(card));
      }
      for (String card : sRuleNames) {
        result.addElement(LookupElementBuilder.create(card));
      }
    }
  }
}
