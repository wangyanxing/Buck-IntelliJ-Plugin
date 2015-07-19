package com.intellij.plugin.buck.format;

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugin.buck.lang.psi.BuckElementType;
import com.intellij.plugin.buck.lang.psi.BuckTypes;
import com.intellij.psi.TokenType;
import com.intellij.psi.formatter.common.AbstractBlock;
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
  //private final BuckBlockContext myContext;
  private List<BuckBlock> mySubBlocks = null;
  private Alignment myChildAlignment;
  private final Alignment myDictAlignment;
  private final Wrap myDictWrapping;
  //private final boolean myEmptySequence;

  public BuckBlock(final BuckBlock parent,
                 final ASTNode node,
                 final Alignment alignment,
                 final Indent indent,
                 final Wrap wrap) {
    myParent = parent;
    myAlignment = alignment;
    myIndent = indent;
    myNode = node;
    myWrap = wrap;
    //myContext = context;
    //myEmptySequence = isEmptySequence(node);

    myDictAlignment = null;
    myDictWrapping = null;
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

    if (parentType == BuckTypes.PROPERTY && grandparentType == BuckTypes.RULE_BODY) {
      childIndent = Indent.getNormalIndent();
    }

    if (parentType == BuckTypes.VALUE_ARRAY) {
      childIndent = Indent.getNormalIndent();
    }
    if (childType == BuckTypes.ARRAY_ELEMENTS) {
      childIndent = Indent.getNormalIndent();
    }

    if (childType == BuckTypes.VALUE_STRING && grandparentType == BuckTypes.ARRAY_ELEMENTS) {
      childIndent = Indent.getNormalIndent();
    }
    return new BuckBlock(this, child, childAlignment, childIndent, wrap);
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
    return new ChildAttributes(Indent.getNormalIndent(), null);
  }

  @Override
  public boolean isIncomplete() {
    return false;
  }

  @Override
  public boolean isLeaf() {
    return myNode.getFirstChildNode() == null;
  }

}

