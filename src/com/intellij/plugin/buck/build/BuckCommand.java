package com.intellij.plugin.buck.build;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * The descriptor of buck command.
 */
public class BuckCommand {

  public static final BuckCommand BUILD = create("build");
  public static final BuckCommand INSTALL = create("install");
  public static final BuckCommand UNINSTALL = create("uninstall");
  public static final BuckCommand KILL = create("kill");

  @NotNull
  @NonNls
  private final String myName; // command name passed to buck

  private BuckCommand(@NotNull String name) {
    myName = name;
  }

  @NotNull
  private static BuckCommand create(@NotNull String name) {
    return new BuckCommand(name);
  }

  @NotNull
  public String name() {
    return myName;
  }

  @Override
  public String toString() {
    return myName;
  }
}
