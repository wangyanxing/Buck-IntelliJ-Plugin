package com.intellij.plugin.buck.build;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Represents a buck build target
 */
public class BuckBuildTarget {

  /**
   * The alias of a target, defined in .buckconfig. For example, fb4a
   */
  private String alias;

  /**
   * Buck target, for example '//third-party/java/dagger:dagger'
   */
  private String target;

  /**
   * Intellij virtual file of the BUCK file
   */
  private VirtualFile virtualFile;

  public BuckBuildTarget(Project project, String target, String alias) {
    this.alias = alias;
    this.target = target;
    this.virtualFile = BuckBuildUtil.getBuckFileFromAbsoluteTarget(project, target);
  }

  public String getAlias() {
    return alias;
  }

  public String getTarget() {
    return target;
  }

  public VirtualFile getVirtualFile() {
    return virtualFile;
  }
}
