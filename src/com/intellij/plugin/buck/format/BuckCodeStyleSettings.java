package com.intellij.plugin.buck.format;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class BuckCodeStyleSettings extends CustomCodeStyleSettings {
  public BuckCodeStyleSettings(CodeStyleSettings container) {
    super("Buck", container);
  }
}
