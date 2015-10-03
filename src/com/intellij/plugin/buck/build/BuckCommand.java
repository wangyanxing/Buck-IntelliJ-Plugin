package com.intellij.plugin.buck.build;

/**
 * The descriptor of buck command.
 */
public class BuckCommand {

  public static final BuckCommand BUILD = create("build");
  public static final BuckCommand INSTALL = create("install");
  public static final BuckCommand UNINSTALL = create("uninstall");
  public static final BuckCommand KILL = create("kill");
  public static final BuckCommand PROJECT = create("project");

  /**
   * Command name passed to buck.
   */
  private final String mName;

  private BuckCommand(String name) {
    mName = name;
  }

  private static BuckCommand create(String name) {
    return new BuckCommand(name);
  }

  public String name() {
    return mName;
  }

  @Override
  public String toString() {
    return mName;
  }
}
