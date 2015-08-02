package com.intellij.plugin.buck.build;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;

import java.util.Iterator;

public class BuckKillCommandHandler extends BuckCommandHandler {

  public BuckKillCommandHandler(final Project project,
                                 final VirtualFile root,
                                 final BuckCommand command) {
    super(project, VfsUtil.virtualToIoFile(root), command);
  }

  @Override
  protected void notifyLines(Key outputType, Iterator<String> lines, StringBuilder lineBuilder) {
  }

  @Override
  protected boolean beforeCommand() {
    if (!BuckBuildManager.getInstance().isBuckProject(myProject)) {
      BuckToolWindowFactory.outputConsoleMessage(
          BuckBuildManager.NOT_BUCK_PROJECT_ERROR_MESSAGE, ConsoleViewContentType.ERROR_OUTPUT);
      return false;
    }
    BuckBuildManager.getInstance().setKilling(true);
    return true;
  }

  @Override
  protected void afterCommand() {
    BuckBuildManager.getInstance().setBuilding(false);
    BuckBuildManager.getInstance().setKilling(false);
    BuckToolWindowFactory.outputConsoleMessage(
        "Build aborted\n", ConsoleViewContentType.ERROR_OUTPUT);
  }
}
