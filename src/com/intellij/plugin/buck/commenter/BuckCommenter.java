package com.intellij.plugin.buck.commenter;

import com.intellij.lang.Commenter;
import org.jetbrains.annotations.Nullable;

/**
 * Defines the support for "Comment with Line Comment" actions in BUCK files
 */
public class BuckCommenter implements Commenter {
  @Nullable
  @Override
  public String getLineCommentPrefix() {
    return "# ";
  }

  @Nullable
  @Override
  public String getBlockCommentPrefix() {
    return null;
  }

  @Nullable
  @Override
  public String getBlockCommentSuffix() {
    return null;
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentPrefix() {
    return null;
  }

  @Nullable
  @Override
  public String getCommentedBlockCommentSuffix() {
    return null;
  }
}
