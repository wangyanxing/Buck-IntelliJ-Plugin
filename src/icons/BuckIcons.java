package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public class BuckIcons {

  private static Icon load(String path) {
    return IconLoader.getIcon(path, BuckIcons.class);
  }

  public static final Icon FILE_TYPE = load("/icons/buck_16.png"); // 16x16
  public static final Icon BUCK_TOOL_WINDOW_ICON = load("/icons/tool_window_13x13.png"); // 13x13
}
