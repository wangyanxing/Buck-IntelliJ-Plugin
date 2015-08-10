package com.intellij.plugin.buck.file;

public final class BuckFileUtil {

  private static final String DEFAULT_BUILD_FILE = "BUCK";
  private static final String SAMPLE_BUCK_FILE =
      "# Thanks for installing Buck Plugin for IDEA!\n" +
          "android_library(\n" +
          "  name = 'bar',\n" +
          "  srcs = glob(['**/*.java']),\n" +
          "  deps = [\n" +
          "    '//android_res/com/foo/interfaces:res',\n" +
          "    '//android_res/com/foo/common/strings:res',\n" +
          "    '//android_res/com/foo/custom:res'\n" +
          "  ],\n" +
          "  visibility = [\n" +
          "    'PUBLIC',\n" +
          "  ],\n" +
          ")\n" +
          "\n" +
          "project_config(\n" +
          "  src_target = ':bar',\n" +
          ")\n";

  private BuckFileUtil() {
  }

  public static String getBuildFileName() {
    // TODO(#7908500): Read from ".buckconfig".
    return DEFAULT_BUILD_FILE;
  }

  public static String getSampleBuckFile() {
    return SAMPLE_BUCK_FILE;
  }
}
