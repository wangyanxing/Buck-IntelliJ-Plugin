package com.intellij.plugin.buck.build;

import com.google.common.collect.Sets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BuckBuildTargetAliasParser {

  static private String ALIAS_PREFIX = "[";
  static private String ALIAS_TAG = "[alias]";
  static private String COMMENT_PREFIX = "#";
  static private char SEPARATOR = '=';

  static public Map<String, Set<String>> sTargetAlias = new HashMap<String, Set<String>>();

  /**
   * Get all alias declared in buck config file.
   *
   * @param baseDir The root folder of the project which contains ".buckconfig"
   */
  static public void parseAlias(String baseDir) {
    sTargetAlias.clear();
    String file = baseDir + File.separatorChar + BuckBuildUtil.BUCK_CONFIG_FILE;
    try {
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;
      boolean seenAliasTag = false;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (!seenAliasTag) {
          if (line.startsWith(ALIAS_TAG)) {
            seenAliasTag = true;
          }
        } else {
          if (line.startsWith(COMMENT_PREFIX)) {
            // Ignore comments
            continue;
          } else if (line.startsWith(ALIAS_PREFIX)) {
            // Another tag
            break;
          } else {
            int separatorIndex = line.indexOf(SEPARATOR);
            if (separatorIndex == -1) {
              continue;
            }
            String alias = line.substring(0, separatorIndex).trim();
            String path = line.substring(separatorIndex + 1).trim();

            if (sTargetAlias.containsKey(path)) {
              sTargetAlias.get(path).add(alias);
            } else {
              sTargetAlias.put(path, Sets.newHashSet(alias));
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
