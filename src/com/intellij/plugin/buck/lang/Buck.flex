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

CRLF= \n|\r|\r\n
WHITE_SPACE=[\ \t\f]
FIRST_VALUE_CHARACTER=[^ \n\r\f\\] | "\\"{CRLF} | "\\".
VALUE_CHARACTER=[^\n\r\f\\] | "\\"{CRLF} | "\\".
END_OF_LINE_COMMENT=("#")[^\r\n]*
SEPARATOR="="
KEY_CHARACTER=[^=\ \n\r\t\f\\] | "\\"{CRLF} | "\\".
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
             "robolectric_test"

KEYWORDS =  "name" |
            "res" |
            "binary_jar" |
            "srcs" |
            "deps" |
            "manifest" |
            "package_type" |
            "keystore" |
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
            "excludes"

DIGIT = [0-9]
LETTER = [:letter:]|"_"
IDENTIFIER = ({LETTER})({LETTER}|{DIGIT})*
VALUE_BOOLEAN = "True" | "False" | "true" | "false" | "TRUE" | "FALSE"

STRING_SINGLE_QUOTED=\'([^\\\'\r\n]|{CRLF})*(\'|\\)? | \'\'\' ( (\'(\')?)? [^\'] )* \'\'\'
STRING_DOUBLE_QUOTED=\"([^\\\"\r\n]|{CRLF})*(\"|\\)? | \"\"\" ( (\"(\")?)? [^\"] )* \"\"\"
STRING = {STRING_SINGLE_QUOTED} | {STRING_DOUBLE_QUOTED}

BRACES = "(" | ")" | "{" | "}" | "[" | "]"
COMMA = ","
SEMICOLON = ";"
EQUAL = "="

%state WAITING_VALUE, DOUBLE_QUOTE_STRING, SINGLE_QUOTE_STRING

%%

{END_OF_LINE_COMMENT}   { return COMMENT; }

{CRLF}                  { return CRLF; }

{WHITE_SPACE}+          { return TokenType.WHITE_SPACE; }

{RULE_NAMES}            { return RULE_NAME; }

{STRING_SINGLE_QUOTED}  { return VALUE_STRING; }

{STRING_DOUBLE_QUOTED}  { return VALUE_STRING; }

{BRACES}                { return BRACES; }

{COMMA}                 { return COMMA; }

{SEMICOLON}             { return SEMICOLON; }

{EQUAL}                 { return EQUAL; }

{VALUE_BOOLEAN}         { return VALUE_BOOLEAN; }

{KEYWORDS}              { return KEYWORDS; }

{IDENTIFIER}            { return IDENTIFIER; }

.                       { return IDENTIFIER; }
