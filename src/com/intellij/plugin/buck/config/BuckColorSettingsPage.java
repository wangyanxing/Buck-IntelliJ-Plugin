package com.intellij.plugin.buck.config;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.plugin.buck.highlight.BuckSyntaxHighlighter;
import icons.BuckIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class BuckColorSettingsPage implements ColorSettingsPage {

  private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
      new AttributesDescriptor("Key", BuckSyntaxHighlighter.BUCK_KEYWORD),
      new AttributesDescriptor("String", BuckSyntaxHighlighter.BUCK_STRING),
      new AttributesDescriptor("Comment", BuckSyntaxHighlighter.BUCK_COMMENT),
      new AttributesDescriptor("Name", BuckSyntaxHighlighter.BUCK_RULE_NAME),
  };

  @Nullable
  @Override
  public Icon getIcon() {
    return BuckIcons.FILE_TYPE;
  }

  @NotNull
  @Override
  public SyntaxHighlighter getHighlighter() {
    return new BuckSyntaxHighlighter();
  }

  @NotNull
  @Override
  public String getDemoText() {
    return "# Thanks for installing Buck Plugin for IDEA!\n" +
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
        "project_config(" +
        "  src_target = ':bar'," +
        ")";
  }

  @Nullable
  @Override
  public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
    return null;
  }

  @NotNull
  @Override
  public AttributesDescriptor[] getAttributeDescriptors() {
    return DESCRIPTORS;
  }

  @NotNull
  @Override
  public ColorDescriptor[] getColorDescriptors() {
    return ColorDescriptor.EMPTY_ARRAY;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Buck";
  }
}
