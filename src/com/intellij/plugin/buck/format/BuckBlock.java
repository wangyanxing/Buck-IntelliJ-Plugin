package com.intellij.plugin.buck.format;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.TokenType;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BuckBlock implements ASTBlock {

    private final BuckBlock myParent;
    private final Alignment myAlignment;
    private final Indent myIndent;
    private final ASTNode myNode;
    private final Wrap myWrap;
    private final Wrap myChildWrap;

    //private final BuckBlockContext myContext;
    private List<BuckBlock> mySubBlocks = null;
    private Alignment myChildAlignment;
    private final Alignment myDictAlignment;
    private final Wrap myDictWrapping;
    private final Alignment myPropertyValueAlignment;
    private final int myIndentSpaceNum;

    public BuckBlock(final BuckBlock parent,
                     final ASTNode node,
                     final Alignment alignment,
                     final Indent indent,
                     final Wrap wrap,
                     final int indentSpaceNum) {
        myParent = parent;
        myAlignment = alignment;
        myIndent = indent;
        myNode = node;
        myWrap = wrap;
        myChildWrap = Wrap.createWrap(2, true);
        myPropertyValueAlignment = Alignment.createAlignment(true);
        myDictAlignment = null;
        myDictWrapping = null;
        myIndentSpaceNum = indentSpaceNum;
    }

    @Override
    public ASTNode getNode() {
        return myNode;
    }

    @NotNull
    @Override
    public TextRange getTextRange() {
        return myNode.getTextRange();
    }

    @NotNull
    @Override
    public List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
            mySubBlocks = buildSubBlocks();
        }
        int a = 0;
        return new ArrayList<Block>(mySubBlocks);
    }

    private List<BuckBlock> buildSubBlocks() {
        final List<BuckBlock> blocks = new ArrayList<BuckBlock>();
        for (ASTNode child = myNode.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            final IElementType childType = child.getElementType();

            if (child.getTextRange().isEmpty()) {
                continue;
            }
            if (childType == TokenType.WHITE_SPACE) {
                continue;
            }
            blocks.add(buildSubBlock(child));
        }
        return Collections.unmodifiableList(blocks);
    }

    private BuckBlock buildSubBlock(ASTNode child) {
        IElementType parentType = myNode.getElementType();
        IElementType childType = child.getElementType();
        IElementType grandparentType =
                myNode.getTreeParent() == null ? null : myNode.getTreeParent().getElementType();

        Wrap wrap = null;
        Indent childIndent = Indent.getNoneIndent();
        Alignment childAlignment = null;
        int indentSpaceNum = 0;
        if (parentType == BuckTypes.ARRAY_ELEMENTS) {
//            if (hasLineBreaksBefore(child, 1)) {
//                indentSpaceNum = 2;
//            }
//            else {
//                indentSpaceNum = 0;
//            }
            indentSpaceNum = 4;
        }
        if (parentType == BuckTypes.PROPERTY) {
            indentSpaceNum = 2;
        }
        if (parentType == BuckTypes.RBRACE) {
            indentSpaceNum = 2;
        }
        childIndent = Indent.getSpaceIndent(myIndentSpaceNum);
        return new BuckBlock(this, child, childAlignment, childIndent, wrap, indentSpaceNum);
    }

    @Nullable
    @Override
    public Wrap getWrap() {
        return myWrap;
    }

    @Nullable
    @Override
    public Indent getIndent() {
        assert myIndent != null;
        return myIndent;
    }

    @Nullable
    @Override
    public Alignment getAlignment() {
        return myAlignment;
    }

    @Nullable
    @Override
    public Spacing getSpacing(@Nullable Block child1, @NotNull Block child2) {
        return null;
    }

    @NotNull
    @Override
    public ChildAttributes getChildAttributes(int newChildIndex) {
        return new ChildAttributes(Indent.getNoneIndent(), null);
    }

    @Override
    public boolean isIncomplete() {
        return false;
    }

    @Override
    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    private static boolean hasLineBreaksBefore(@NotNull ASTNode child, int minCount) {
        final ASTNode treePrev = child.getTreePrev();
        return (treePrev != null && isWhitespaceWithLineBreaks(TreeUtil.findLastLeaf(treePrev), minCount)) ||
                isWhitespaceWithLineBreaks(child.getFirstChildNode(), minCount);
    }

    private static boolean isWhitespaceWithLineBreaks(@Nullable ASTNode node, int minCount) {
        if (isWhitespace(node)) {
            final String prevNodeText = node.getText();
            int count = 0;
            for (int i = 0; i < prevNodeText.length(); i++) {
                if (prevNodeText.charAt(i) == '\n') {
                    count++;
                    if (count == minCount) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isWhitespace(@Nullable ASTNode node) {
        return node != null && (node.getElementType() == TokenType.WHITE_SPACE);
    }
}

