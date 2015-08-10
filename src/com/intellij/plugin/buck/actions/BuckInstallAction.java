package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.build.BuckBuildCommandHandler;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.build.BuckCommand;
import com.intellij.plugin.buck.config.BuckSettingsProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Run buck install command
 */
public class BuckInstallAction extends DumbAwareAction {

  public static final String ACTION_TITLE = "Run buck install";
  public static final String ACTION_DESCRIPTION = "Run buck install command";

  public BuckInstallAction() {
    super(ACTION_TITLE, ACTION_DESCRIPTION, AllIcons.Actions.Execute);
  }

  @Override
  public void update(AnActionEvent e) {
    Project project = e.getProject();
    if (project != null) {
      e.getPresentation().setEnabled(!BuckBuildManager.getInstance(project).isBuilding());
    }

  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    BuckBuildManager buildManager = BuckBuildManager.getInstance(e.getProject());
    String target = buildManager.getCurrentSavedTarget(e.getProject());
    if (target == null) {
      buildManager.showNoTargetMessage(e.getProject());
      return;
    }

    BuckSettingsProvider.State state = BuckSettingsProvider.getInstance().getState();
    if (state == null) {
      return;
    }

    BuckBuildCommandHandler handler = new BuckBuildCommandHandler(
        e.getProject(),
        e.getProject().getBaseDir(),
        BuckCommand.INSTALL);
    if (state.customizedInstallSetting) {
      // Split the whole command line into different parameters.
      String commands = state.customizedInstallSettingCommand;
      Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(commands);
      while (matcher.find()) {
        handler.command().addParameter(matcher.group(1));
      }
    } else {
      if (state.runAfterInstall) {
        handler.command().addParameter("-r");
      }
      if (state.multiInstallMode) {
        handler.command().addParameter("-x");
      }
      if (state.uninstallBeforeInstalling) {
        handler.command().addParameter("-u");
      }
    }
    handler.command().addParameter(target);
    buildManager.runBuckCommand(handler, ACTION_TITLE);
  }
}
