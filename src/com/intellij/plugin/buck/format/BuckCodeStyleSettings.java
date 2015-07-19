package com.intellij.plugin.buck.format;

import com.intellij.formatting.WrapType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

public class BuckCodeStyleSettings extends CustomCodeStyleSettings {
  public enum DictAlignment {
    NONE("formatter.panel.dict.alignment.do.not.align"),
    ON_VALUE("formatter.panel.dict.alignment.align.on.value"),
    ON_COLON("formatter.panel.dict.alignment.align.on.colon");

    String description;

    DictAlignment(String description) {
      this.description = description;
    }

    public int asInt() {
      return ordinal();
    }

    @Override
    public String toString() {
      return description;
    }
  }

  // Unfortunately, the old serializer for code style settings can't handle enums
  public static final int DICT_ALIGNMENT_NONE = DictAlignment.NONE.asInt();
  public static final int DICT_ALIGNMENT_ON_VALUE = DictAlignment.ON_VALUE.asInt();
  public static final int DICT_ALIGNMENT_ON_COLON = DictAlignment.ON_COLON.asInt();

  public boolean SPACE_WITHIN_BRACES = false;
  public boolean SPACE_BEFORE_PY_COLON = false;
  public boolean SPACE_AFTER_PY_COLON = true;
  public boolean SPACE_BEFORE_LBRACKET = false;
  public boolean SPACE_AROUND_EQ_IN_NAMED_PARAMETER = false;
  public boolean SPACE_AROUND_EQ_IN_KEYWORD_ARGUMENT = false;
  public boolean SPACE_BEFORE_BACKSLASH = true;

  public int BLANK_LINES_AROUND_TOP_LEVEL_CLASSES_FUNCTIONS = 2;
  public boolean BLANK_LINE_AT_FILE_END = true;

  public boolean ALIGN_COLLECTIONS_AND_COMPREHENSIONS = true;
  public boolean ALIGN_MULTILINE_IMPORTS = true;

  public boolean NEW_LINE_AFTER_COLON = false;
  public boolean NEW_LINE_AFTER_COLON_MULTI_CLAUSE = true;

  public boolean SPACE_AFTER_NUMBER_SIGN = true;
  public boolean SPACE_BEFORE_NUMBER_SIGN = true;

  public int DICT_ALIGNMENT = DICT_ALIGNMENT_NONE;
  public int DICT_WRAPPING = WrapType.NORMAL.getLegacyRepresentation();
  public boolean DICT_NEW_LINE_AFTER_LEFT_BRACE = false;
  public boolean DICT_NEW_LINE_BEFORE_RIGHT_BRACE = false;

  public int BLANK_LINES_AFTER_LOCAL_IMPORTS = 0;


  public BuckCodeStyleSettings(CodeStyleSettings container) {
    super("Buck", container);
  }
}
