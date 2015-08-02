package com.intellij.plugin.buck.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Set;
import java.lang.String;

%%

%class _BuckLexer
%implements FlexLexer, BuckTypes
%unicode
%public
%function advance
%type IElementType
%eof{  return;
%eof}

WHITE_SPACE = [\ \t\f]|\n|\r|\r\n
FIRST_VALUE_CHARACTER = [^ \n\r\f\\] | "\\"{WHITE_SPACE} | "\\".
VALUE_CHARACTER = [^\n\r\f\\] | "\\"{WHITE_SPACE} | "\\".
END_OF_LINE_COMMENT = ("#")[^\r\n]*
KEY_CHARACTER = [^=\ \n\r\t\f\\] | "\\"{WHITE_SPACE} | "\\".
RULE_NAMES = "genrule"|
             "remote_file"|
             "android_aar"|
             "android_binary"|
             "android_build_config"|
             "android_library"|
             "android_manifest"|
             "android_prebuilt_aar"|
             "android_resource"|
             "apk_genrule"|
             "cxx_library"|
             "gen_aidl"|
             "ndk_library"|
             "prebuilt_jar"|
             "prebuilt_native_library"|
             "project_config"|
             "cxx_binary"|
             "cxx_library"|
             "cxx_test"|
             "prebuilt_native_library"|
             "d_binary"|
             "d_library"|
             "d_test"|
             "cxx_library"|
             "java_binary"|
             "java_library"|
             "java_test"|
             "prebuilt_jar"|
             "prebuilt_native_library"|
             "prebuilt_python_library"|
             "python_binary"|
             "python_library"|
             "python_test"|
             "glob"|
             "include_defs"|
             "robolectric_test"|
             "keystore"

GENERIC_RULE_NAMES = [a-zA-Z0-9]+("_android_library") | [a-zA-Z0-9]+("_android_library")

KEYWORDS =  "name" |
            "res" |
            "binary_jar" |
            "srcs" |
            "deps" |
            "manifest" |
            "package_type" |
            "glob" |
            "visibility" |
            "aar" |
            "src_target" |
            "src_roots" |
            "java7_support" |
            "source_under_test" |
            "test_library_project_dir" |
            "contacts" |
            "exported_deps" |
            "excludes" |
            "main" |
            "resources" |
            "javadoc_url" |
            "store" |
            "properties" |
            "assets" |
            "package" |
            "proguard_config" |
            "source_jar" |
            "aidl" |
            "import_path" |
            "annotation_processors" |
            "annotation_processor_deps" |
            "keystore"

MACROS = ([A-Z0-9] | ("_"))+

DIGIT = [0-9]
LETTER = [:letter:]|"_"
IDENTIFIER = ({LETTER})({LETTER}|{DIGIT})*
VALUE_BOOLEAN = "True" | "False" | "true" | "false" | "TRUE" | "FALSE"
VALUE_NONE = "None"

STRING_SINGLE_QUOTED = \'([^\\\'\r\n]|{WHITE_SPACE})*(\'|\\)? | \'\'\' ( (\'(\')?)? [^\'] )* \'\'\'
STRING_DOUBLE_QUOTED = \"([^\\\"\r\n]|{WHITE_SPACE})*(\"|\\)? | \"\"\" ( (\"(\")?)? [^\"] )* \"\"\"
STRING = {STRING_SINGLE_QUOTED} | {STRING_DOUBLE_QUOTED}

COMMA = ","
EQUAL = "="

L_PARENTHESES = "("
L_BRACKET = "["
R_PARENTHESES = ")"
R_BRACKET = "]"

%state WAITING_VALUE, DOUBLE_QUOTE_STRING, SINGLE_QUOTE_STRING

%%

{END_OF_LINE_COMMENT}   { return COMMENT; }

{WHITE_SPACE}+          { return TokenType.WHITE_SPACE; }

{RULE_NAMES}            { return RULE_NAMES; }

{GENERIC_RULE_NAMES}    { return RULE_NAMES; }

{STRING_SINGLE_QUOTED}  { return VALUE_STRING; }

{STRING_DOUBLE_QUOTED}  { return VALUE_STRING; }

{L_PARENTHESES}         { return L_PARENTHESES; }

{L_BRACKET}             { return L_BRACKET; }

{R_PARENTHESES}         { return R_PARENTHESES; }

{R_BRACKET}             { return R_BRACKET; }

{COMMA}                 { return COMMA; }

{EQUAL}                 { return EQUAL; }

{VALUE_BOOLEAN}         { return VALUE_BOOLEAN; }

{VALUE_NONE}            { return VALUE_NONE; }

{KEYWORDS}              { return KEYWORDS; }

{MACROS}                { return MACROS; }

{IDENTIFIER}            { return IDENTIFIER; }

.                       { return IDENTIFIER; }
