package com.intellij.plugin.buck.lang.psi;

import com.intellij.plugin.buck.file.BuckFileType;
import com.intellij.psi.tree.IElementType;

public class BuckElementType extends IElementType {
    public BuckElementType(String debugName){
        super(debugName, BuckFileType.INSTANCE.getLanguage());
    }
}
