package com.intellij.plugin.buck.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Load and save buck setting states across IDE restarts
 */
@State(
    name = "BuckOptionsProvider",
    storages = {
        @Storage(
            file = StoragePathMacros.APP_CONFIG + "/buck.xml"
        )}
)
public class BuckSettingsProvider implements PersistentStateComponent<BuckSettingsProvider.State>,
    ExportableApplicationComponent {
  private State myState = new State();

  public static BuckSettingsProvider getInstance() {
    return ApplicationManager.getApplication().getComponent(BuckSettingsProvider.class);
  }

  @Override
  public State getState() {
    return myState;
  }

  @Override
  public void loadState(State state) {
    myState = state;
  }

  @Override
  public void initComponent() {
  }

  @Override
  public void disposeComponent() {
  }

  @NotNull
  @Override
  public File[] getExportFiles() {
    return new File[]{new File(PathManager.getOptionsPath() + File.separatorChar + "buck.xml")};
  }

  @NotNull
  @Override
  public String getPresentableName() {
    return "BuckOptions";
  }

  @NotNull
  @Override
  public String getComponentName() {
    return "BuckOptionsProvider";
  }

  /**
   * All settings are stored in this inner class
   */
  public static class State {

    /**
     * Remember the last used buck alias for each historical project.
     */
    public Map<String, String> lastAlias = new HashMap<String, String>();

    /**
     * Path to buck executable.
     */
    public String buckExecutable = BuckExecutableDetector.detect();

    /**
     * "-r" parameter for "buck install"
     */
    public Boolean runAfterInstall = true;

    /**
     * "-x" parameter for "buck install"
     */
    public Boolean multiInstallMode = false;

    /**
     * "-u" parameter for "buck install"
     */
    public Boolean uninstallBeforeInstalling = false;

    /**
     * If use user's customized install string.
     */
    public Boolean customizedInstallSetting = false;

    /**
     * User's customized install command string, e.g. "-a -b -c".
     */
    public String customizedInstallSettingCommand = "";
  }
}
