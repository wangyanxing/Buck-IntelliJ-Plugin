package com.intellij.plugin.buck.lang;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.plugin.buck.file.BuckFileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class BuckFile extends PsiFileBase {

  public BuckFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, BuckLanguage.INSTANCE);
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return BuckFileType.INSTANCE;
  }

  @Override
  public String toString() {
    return "Buck File";
  }

  @Override
  public Icon getIcon(int flags) {
    return super.getIcon(flags);
  }
}
