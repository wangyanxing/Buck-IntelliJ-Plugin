package com.intellij.plugin.buck.lang;

import com.intellij.lang.Language;

/**
 * Created by cjlm on 6/14/15.
 */
public class BuckLanguage extends Language {

    public static final BuckLanguage INSTANCE = new BuckLanguage();

    public BuckLanguage(){
        super("Buck");
    }
}
