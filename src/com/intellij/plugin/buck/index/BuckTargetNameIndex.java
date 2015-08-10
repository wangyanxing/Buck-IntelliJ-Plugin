package com.intellij.plugin.buck.index;

import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.file.BuckFileType;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class BuckTargetNameIndex extends ScalarIndexExtension<String> {

  public static final ID<String, Void> NAME = ID.create("Buck.target.name");

  private final EnumeratorStringDescriptor mKeyDescriptor = new EnumeratorStringDescriptor();
  private final DataIndexer<String, Void, FileContent> mDataIndexer =
      new DataIndexer<String, Void, FileContent>() {
    @Override
    public Map<String, Void> map(FileContent inputData) {
      return Collections.singletonMap(inputData.getFile().getPath(), null);
    }
  };

  @Override
  public ID<String, Void> getName() {
    return NAME;
  }

  @Override
  public DataIndexer<String, Void, FileContent> getIndexer() {
    return mDataIndexer;
  }

  @Override
  public KeyDescriptor<String> getKeyDescriptor() {
    return mKeyDescriptor;
  }

  @Override
  public FileBasedIndex.InputFilter getInputFilter() {
    return new DefaultFileTypeSpecificInputFilter(BuckFileType.INSTANCE);
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }

  @Override
  public int getVersion() {
    return 0;
  }

  public static Collection<String> getAllKeys(Project project) {
    return FileBasedIndex.getInstance().getAllKeys(NAME, project);
  }
}
