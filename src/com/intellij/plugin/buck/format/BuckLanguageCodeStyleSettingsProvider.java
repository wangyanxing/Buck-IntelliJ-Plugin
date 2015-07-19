package com.intellij.plugin.buck.format;

import com.intellij.application.options.IndentOptionsEditor;
import com.intellij.application.options.SmartIndentOptionsEditor;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationBundle;
import com.intellij.plugin.buck.lang.BuckLanguage;
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.codeStyle.DisplayPriority;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.intellij.util.PlatformUtils;
import org.jetbrains.annotations.NotNull;

import static com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable.*;

public class BuckLanguageCodeStyleSettingsProvider extends LanguageCodeStyleSettingsProvider {
    @NotNull
    @Override
    public Language getLanguage() {
        return BuckLanguage.INSTANCE;
    }

    @Override
    public void customizeSettings(@NotNull CodeStyleSettingsCustomizable consumer, @NotNull SettingsType settingsType) {
        if (settingsType == SettingsType.SPACING_SETTINGS) {
            consumer.showStandardOptions("SPACE_BEFORE_METHOD_CALL_PARENTHESES",
                    "SPACE_BEFORE_METHOD_PARENTHESES",
                    "SPACE_AROUND_ASSIGNMENT_OPERATORS",
                    "SPACE_AROUND_EQUALITY_OPERATORS",
                    "SPACE_AROUND_RELATIONAL_OPERATORS",
                    "SPACE_AROUND_BITWISE_OPERATORS",
                    "SPACE_AROUND_ADDITIVE_OPERATORS",
                    "SPACE_AROUND_MULTIPLICATIVE_OPERATORS",
                    "SPACE_AROUND_SHIFT_OPERATORS",
                    "SPACE_WITHIN_METHOD_CALL_PARENTHESES",
                    "SPACE_WITHIN_METHOD_PARENTHESES",
                    "SPACE_WITHIN_BRACKETS",
                    "SPACE_AFTER_COMMA",
                    "SPACE_BEFORE_COMMA",
                    "SPACE_BEFORE_SEMICOLON");
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_BEFORE_LBRACKET",
                    "formatter.left.bracket", SPACES_BEFORE_PARENTHESES);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_AROUND_EQ_IN_NAMED_PARAMETER",
                    "formatter.around.eq.in.named.parameter", SPACES_AROUND_OPERATORS);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_AROUND_EQ_IN_KEYWORD_ARGUMENT",
                    "formatter.around.eq.in.keyword.argument", SPACES_AROUND_OPERATORS);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_WITHIN_BRACES", "formatter.braces", SPACES_WITHIN);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_BEFORE_PY_COLON",
                    ApplicationBundle.message("checkbox.spaces.before.colon"), SPACES_OTHER);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_AFTER_PY_COLON",
                    ApplicationBundle.message("checkbox.spaces.after.colon"), SPACES_OTHER);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_BEFORE_BACKSLASH",
                    "formatter.before.backslash", SPACES_OTHER);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_BEFORE_NUMBER_SIGN",
                    "formatter.before.hash", SPACES_OTHER);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "SPACE_AFTER_NUMBER_SIGN",
                    "formatter.after.hash", SPACES_OTHER);
        }
        else if (settingsType == SettingsType.BLANK_LINES_SETTINGS) {
            consumer.showStandardOptions("BLANK_LINES_AROUND_CLASS",
                    "BLANK_LINES_AROUND_METHOD",
                    "BLANK_LINES_AFTER_IMPORTS",
                    "KEEP_BLANK_LINES_IN_DECLARATIONS",
                    "KEEP_BLANK_LINES_IN_CODE");
            consumer.renameStandardOption("BLANK_LINES_AFTER_IMPORTS", "formatter.around.top.level.imports");

            consumer.showCustomOption(BuckCodeStyleSettings.class, "BLANK_LINES_AROUND_TOP_LEVEL_CLASSES_FUNCTIONS",
                    "formatter.around.top.level.classes.and.function", BLANK_LINES);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "BLANK_LINES_AFTER_LOCAL_IMPORTS",
                    "formatter.after.local.imports", BLANK_LINES);
        }
        else if (settingsType == SettingsType.WRAPPING_AND_BRACES_SETTINGS) {
            consumer.showStandardOptions("RIGHT_MARGIN",
                    "KEEP_LINE_BREAKS",
                    "WRAP_LONG_LINES",
                    "ALIGN_MULTILINE_PARAMETERS",
                    "ALIGN_MULTILINE_PARAMETERS_IN_CALLS");
            consumer.showCustomOption(BuckCodeStyleSettings.class, "NEW_LINE_AFTER_COLON",
                    "formatter.single.clause.statements",
                    "formatter.force.new.line.after.colon");
            consumer.showCustomOption(BuckCodeStyleSettings.class, "NEW_LINE_AFTER_COLON_MULTI_CLAUSE",
                    "formatter.multi.clause.statements",
                    "formatter.force.new.line.after.colon");
            consumer.showCustomOption(BuckCodeStyleSettings.class, "ALIGN_COLLECTIONS_AND_COMPREHENSIONS",
                    "formatter.align.when.multiline",
                    "formatter.collections.and.comprehensions");
            consumer.showCustomOption(BuckCodeStyleSettings.class, "ALIGN_MULTILINE_IMPORTS",
                    "formatter.align.when.multiline",
                    "formatter.import.statements");

            consumer.showCustomOption(BuckCodeStyleSettings.class, "DICT_WRAPPING",
                    "formatter.dictionary.literals", null, WRAP_OPTIONS, WRAP_VALUES);
            consumer.showCustomOption(BuckCodeStyleSettings.class, "DICT_NEW_LINE_AFTER_LEFT_BRACE",
                    ApplicationBundle.message("wrapping.new.line.after.lbrace"),
                    "formatter.dictionary.literals");
            consumer.showCustomOption(BuckCodeStyleSettings.class, "DICT_NEW_LINE_BEFORE_RIGHT_BRACE",
                    ApplicationBundle.message("wrapping.rbrace.on.new.line"),
                    "formatter.dictionary.literals");
        }
    }

    @Override
    public IndentOptionsEditor getIndentOptionsEditor() {
        return new SmartIndentOptionsEditor();
    }

    @Override
    public CommonCodeStyleSettings getDefaultCommonSettings() {
        CommonCodeStyleSettings defaultSettings = new CommonCodeStyleSettings(BuckLanguage.INSTANCE);
        CommonCodeStyleSettings.IndentOptions indentOptions = defaultSettings.initIndentOptions();
        indentOptions.INDENT_SIZE = 4;
        defaultSettings.ALIGN_MULTILINE_PARAMETERS_IN_CALLS = true;
        defaultSettings.KEEP_BLANK_LINES_IN_DECLARATIONS = 1;
        defaultSettings.KEEP_BLANK_LINES_IN_CODE = 1;
        defaultSettings.RIGHT_MARGIN = 99;
        return defaultSettings;
    }

    @Override
    public DisplayPriority getDisplayPriority() {
        return PlatformUtils.isPyCharm() ? DisplayPriority.KEY_LANGUAGE_SETTINGS : DisplayPriority.LANGUAGE_SETTINGS;
    }

    @Override
    public String getCodeSample(@NotNull SettingsType settingsType) {
        return "Thanks for installing IntelliJ IEDA Buck Plugin!";
    }
}