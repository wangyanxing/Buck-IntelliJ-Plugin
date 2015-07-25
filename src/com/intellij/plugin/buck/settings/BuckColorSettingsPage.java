package com.intellij.plugin.buck.settings;


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
            new AttributesDescriptor("Key", BuckSyntaxHighlighter.KEY),
            new AttributesDescriptor("Value", BuckSyntaxHighlighter.VALUE),
            new AttributesDescriptor("Comment", BuckSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Name", BuckSyntaxHighlighter.RULE_NAME),
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
        return "# Thanks for installing IntelliJ IEDA Buck Plugin!\n" +
                "android_library(\n" +
                "  name = 'adinterfaces',\n" +
                "  srcs = glob(['**/*.java']),\n" +
                "  deps = [\n" +
                "    '//android_res/com/facebook/adinterfaces:res',\n" +
                "    '//android_res/com/facebook/common/strings:res',\n" +
                "    '//android_res/com/facebook/custom:res'\n" +
                "  ],\n" +
                "  visibility = [\n" +
                "    'PUBLIC',\n" +
                "  ],\n\n" +
                "project_config(" +
                "  src_target = ':adinterfaces'," +
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