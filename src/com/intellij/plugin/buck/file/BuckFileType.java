package com.intellij.plugin.buck.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.plugin.buck.lang.BuckLanguage;
import icons.BuckIcons;

import javax.swing.Icon;

/**
 * Buck language type
 */
public class BuckFileType extends LanguageFileType {

  public static final BuckFileType INSTANCE = new BuckFileType();
  private static final String[] DEFAULT_EXTENSIONS = {"BUCK", ""};

  private BuckFileType() {
    super(BuckLanguage.INSTANCE);
  }

  public String getName() {
    return "Buck";
  }

  public String getDescription() {
    return "Buck file";
  }

  public String getDefaultExtension() {
    return DEFAULT_EXTENSIONS[0];
  }

  public Icon getIcon() {
    return BuckIcons.FILE_TYPE;
  }
}
