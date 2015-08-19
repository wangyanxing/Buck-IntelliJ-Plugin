package com.intellij.plugin.buck.index;

import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.file.BuckFileType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.DataIndexer;
import com.intellij.util.indexing.DefaultFileTypeSpecificInputFilter;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.FileContent;
import com.intellij.util.indexing.ID;
import com.intellij.util.indexing.ScalarIndexExtension;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
    return new DefaultFileTypeSpecificInputFilter(BuckFileType.INSTANCE) {
      @Override
      public boolean acceptInput(@NotNull final VirtualFile file) {

        return file.isInLocalFileSystem();
      }
    };
  }

  @Override
  public boolean dependsOnFileContent() {
    return true;
  }

  @Override
  public int getVersion() {
    return 0;
  }

  public static Collection<VirtualFile> getAllFiles(Project project) {
    final GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
    final Collection<String> files = FileBasedIndex.getInstance().getAllKeys(NAME, project);
    Set<VirtualFile> result = Sets.newHashSet();
    for (Iterator<String> iterator = files.iterator(); iterator.hasNext(); ) {
      final String fileName = iterator.next();
      VirtualFile file = VfsUtil.findFileByIoFile(new File(fileName), true);
      if (file != null && searchScope.contains(file)) {
        result.add(file);
      }
    }
    return result;
  }
}
