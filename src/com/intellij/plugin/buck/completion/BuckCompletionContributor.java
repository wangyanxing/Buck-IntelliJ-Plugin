package com.intellij.plugin.buck.completion;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BuckCompletionContributor extends CompletionContributor {

  public static final ArrayList<String> sKeywords = new ArrayList<String>() {{
    add("name");
    add("res");
    add("binary_jar");
    add("srcs");
    add("deps");
    add("manifest");
    add("package_type");
    add("keystore");
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
    add("keystore");
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
  }};

  public BuckCompletionContributor() {
    extend(CompletionType.BASIC,
        PlatformPatterns.psiElement(BuckTypes.IDENTIFIER).withLanguage(BuckLanguage.INSTANCE),
        new CompletionProvider<CompletionParameters>() {
          public void addCompletions(@NotNull CompletionParameters parameters,
                                     ProcessingContext context,
                                     @NotNull CompletionResultSet resultSet) {
            for (String card : sKeywords) {
              resultSet.addElement(LookupElementBuilder.create(card));
            }
            for (String card : sRuleNames) {
              resultSet.addElement(LookupElementBuilder.create(card));
            }
          }
        }
    );
  }
}
