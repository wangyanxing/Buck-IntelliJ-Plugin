package com.intellij.plugin.buck.lang;

import com.intellij.lang.Language;

public class BuckLanguage extends Language {

  public static final BuckLanguage INSTANCE = new BuckLanguage();

  public BuckLanguage() {
    super("Buck");
  }
}
