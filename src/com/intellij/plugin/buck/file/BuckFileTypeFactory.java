package com.intellij.plugin.buck.file;

import com.intellij.openapi.fileTypes.ExactFileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class BuckFileTypeFactory extends FileTypeFactory {

  @Override
  public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
    fileTypeConsumer.consume(BuckFileType.INSTANCE, new ExactFileNameMatcher("BUCK"));
  }
}