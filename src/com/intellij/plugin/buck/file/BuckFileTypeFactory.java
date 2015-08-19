package com.intellij.plugin.buck.file;

import com.intellij.openapi.fileTypes.FileNameMatcherEx;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtilRt;

public class BuckFileTypeFactory extends FileTypeFactory {

  @Override
  public void createFileTypes(FileTypeConsumer fileTypeConsumer) {
    fileTypeConsumer.consume(
        BuckFileType.INSTANCE, new FileNameMatcherEx() {
          @Override
          public String getPresentableString() {
            return BuckFileUtil.getBuildFileName();
          }

          @Override
          public boolean acceptsCharSequence(CharSequence fileName) {
            String buildFileName = BuckFileUtil.getBuildFileName();
            return StringUtilRt.endsWithIgnoreCase(fileName, buildFileName) ||
              Comparing.equal(fileName, buildFileName, true);
          }
        });
  }
}