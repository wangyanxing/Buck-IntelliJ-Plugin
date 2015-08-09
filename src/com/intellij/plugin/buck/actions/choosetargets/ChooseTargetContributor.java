package com.intellij.plugin.buck.actions.choosetargets;

import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.build.BuckBuildTargetAliasParser;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.util.ArrayUtil;

import java.util.Set;

public class ChooseTargetContributor implements ChooseByNameContributor {

  @Override
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    String path = project.getBasePath();
    BuckBuildTargetAliasParser.parseAlias(path);

    Set<String> alias = BuckBuildTargetAliasParser.sTargetAlias.keySet();
    return ArrayUtil.toStringArray(alias);
  }

  @Override
  public NavigationItem[] getItemsByName(
      String name,
      String pattern,
      Project project,
      boolean includeNonProjectItems) {
    String target = BuckBuildTargetAliasParser.sTargetAlias.get(name);
    VirtualFile file = BuckBuildUtil.getBuckFileFromAbsoluteTarget(project, target);
    if (file == null) {
      return new NavigationItem[0];
    }

    return new NavigationItem[] { new ChooseTargetItem(name, target) };
  }
}
