package com.intellij.plugin.buck.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.plugin.buck.lang.BuckLanguage;
import icons.BuckIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BuckFileType extends LanguageFileType {

  public static final BuckFileType INSTANCE = new BuckFileType();
  private static final String[] DEFAULT_EXTENSIONS = {"BUCK", ""};

  private BuckFileType() {
    super(BuckLanguage.INSTANCE);
  }

  @NotNull
  public String getName() {
    return "Buck";
  }

  @NotNull
  public String getDescription() {
    return "Buck file";
  }

  @NotNull
  public String getDefaultExtension() {
    return DEFAULT_EXTENSIONS[0];
  }

  public Icon getIcon() {
    return BuckIcons.FILE_TYPE;
  }
}
