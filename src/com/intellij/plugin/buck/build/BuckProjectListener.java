package com.intellij.plugin.buck.build;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;

public class BuckProjectListener implements ProjectManagerListener {

  public BuckProjectListener() {
    ProjectManager.getInstance().addProjectManagerListener(this);
  }

  @Override
  public void projectOpened(Project project) {
    BuckBuildManager.getInstance().setBuckProject(
        project.getBaseDir().findChild(BuckBuildUtil.BUCK_CONFIG_FILE) != null);
  }

  @Override
  public boolean canCloseProject(Project project) {
    return true;
  }

  @Override
  public void projectClosed(Project project) {
    BuckBuildManager.getInstance().setBuckProject(false);
  }

  @Override
  public void projectClosing(Project project) {
  }
}
