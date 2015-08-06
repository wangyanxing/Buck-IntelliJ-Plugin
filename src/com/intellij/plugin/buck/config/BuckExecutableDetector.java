package com.intellij.plugin.buck.config;

import com.intellij.openapi.util.SystemInfo;

import java.io.File;

public class BuckExecutableDetector {

  private static final String[] UNIX_PATHS = {
      "/usr/local/bin",
      "/usr/bin",
      "/opt/local/bin",
      "/opt/bin",
  };
  private static final String UNIX_EXECUTABLE = "buck";

  public static String detect() {
    if (SystemInfo.isWindows) {
      return null;
    }
    return detectForUnix();
  }

  private static String detectForUnix() {
    for (String p : UNIX_PATHS) {
      File f = new File(p, UNIX_EXECUTABLE);
      if (f.exists()) {
        return f.getPath();
      }
    }
    return null;
  }
}
