package com.intellij.plugin.buck.actions.choosetargets;

import com.intellij.ide.util.gotoByName.FilteringGotoByModel;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.plugin.buck.build.BuckBuildTarget;
import org.jetbrains.annotations.Nullable;

public class ChooseTargetModel
    extends FilteringGotoByModel<BuckBuildTarget> implements DumbAware {

  public ChooseTargetModel(Project project) {
    super(project, new ChooseByNameContributor[] { new ChooseTargetContributor() });
  }

  @Nullable
  @Override
  protected BuckBuildTarget filterValueFor(NavigationItem navigationItem) {
    return null;
  }

  @Override
  public String getPromptText() {
    return "Enter Buck build alias or full target";
  }

  @Override
  public String getNotInMessage() {
    return "No matches found";
  }

  @Override
  public String getNotFoundMessage() {
    return "No targets found";
  }

  @Nullable
  @Override
  public String getCheckBoxName() {
    return null;
  }

  @Override
  public char getCheckBoxMnemonic() {
    return SystemInfo.isMac ? 'P' : 'n';
  }

  @Override
  public boolean loadInitialCheckBoxState() {
    return false;
  }

  @Override
  public void saveInitialCheckBoxState(boolean state) {
  }

  @Override
  public String[] getSeparators() {
    return new String[0];
  }

  @Override
  public String getFullName(Object element) {
    return getElementName(element);
  }

  @Override
  public boolean willOpenEditor() {
    return false;
  }
}
