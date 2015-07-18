package com.intellij.plugin.buck.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@State(
    name = "BuckOptionsProvider",
    storages = {
        @Storage(
            file = StoragePathMacros.APP_CONFIG + "/buck.xml"
        )}
)
public class BuckOptionsProvider implements PersistentStateComponent<BuckOptionsProvider.State>,
    ExportableApplicationComponent {
  private State myState = new State();

  public static BuckOptionsProvider getInstance() {
    return ApplicationManager.getApplication().getComponent(BuckOptionsProvider.class);
  }

  @Override
  public State getState() {
    return myState;
  }

  @Override
  public void loadState(State state) {
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

  public static class State {
  }
}
