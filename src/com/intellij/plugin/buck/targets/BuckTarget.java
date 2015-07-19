package com.intellij.plugin.buck.targets;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class BuckTarget {

  private String alias;
  private String target;
  private VirtualFile virtualFile;

  public BuckTarget(Project project, String target, String alias) {
    this.alias = alias;
    this.target = target;
    this.virtualFile = BuckTargetUtil.getBuckFileFromAbsoluteTarget(project, target);
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
