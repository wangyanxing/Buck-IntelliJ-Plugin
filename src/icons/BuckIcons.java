package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class BuckIcons {

  private static Icon load(String path) {
    return IconLoader.getIcon(path, BuckIcons.class);
  }

  public static final Icon FILE_TYPE = load("/icons/buck_icon.png"); // 32x32
  public static final Icon BUCK_INSTALL = load("/icons/install_16x16.png"); // 16x16
  public static final Icon BUCK_KILL = load("/icons/stop_16x16.png"); // 16x16
  public static final Icon GOTO_BUCK_FILE = load("/icons/goto_16x16.png"); // 16x16
  public static final Icon BUCK_TOOL_WINDOW_ICON =
      load("/icons/buck_tool_window_icon.png"); // 13x13
}
