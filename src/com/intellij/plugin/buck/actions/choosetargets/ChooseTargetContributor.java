package com.intellij.plugin.buck.actions.choosetargets;

import com.google.common.base.Joiner;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.build.BuckBuildTargetAliasParser;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.plugin.buck.index.BuckTargetNameIndex;

import java.io.File;
import java.util.*;

public class ChooseTargetContributor implements ChooseByNameContributor {

  public static final String ALIAS_SEPARATOR = "::";

  @Override
  public String[] getNames(Project project, boolean includeNonProjectItems) {
    BuckBuildTargetAliasParser.parseAlias(project.getBasePath());
    List<String> names = new ArrayList<String>();

    Collection<VirtualFile> buckKeys = BuckTargetNameIndex.getAllFiles(project);
    for(VirtualFile file : buckKeys) {
      String target = BuckBuildUtil.getFullBuckTarget(project, file);
      Set<String> alias = BuckBuildTargetAliasParser.sTargetAlias.get(target);
      if (alias == null) {
        names.add(target);
      }
    }

    for (Map.Entry<String, Set<String>> entry :
        BuckBuildTargetAliasParser.sTargetAlias.entrySet()) {
      String target = entry.getKey();
      Set<String> alias = entry.getValue();
      target += ALIAS_SEPARATOR + Joiner.on(',').join(alias);
      names.add(target);
    }

    return names.toArray(new String[names.size()]);
  }

  @Override
  public NavigationItem[] getItemsByName(
      String name,
      String pattern,
      Project project,
      boolean includeNonProjectItems) {
    String alias = null;
    int index = name.lastIndexOf(ALIAS_SEPARATOR);
    if (index > 0) {
      alias = name.substring(index + ALIAS_SEPARATOR.length());
      name = name.substring(0, index);
    }
    return new NavigationItem[] { new ChooseTargetItem(name, alias) };
  }
}
