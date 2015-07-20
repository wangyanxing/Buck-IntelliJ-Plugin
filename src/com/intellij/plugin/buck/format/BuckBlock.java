package com.intellij.plugin.buck.format;

import com.intellij.formatting.*;
import com.intellij.json.psi.JsonArray;
import com.intellij.json.psi.JsonObject;
import com.intellij.json.psi.JsonPsiUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.plugin.buck.lang.psi.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CommonCodeStyleSettings;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intellij.plugin.buck.lang.psi.BuckPsiUtils.hasElementType;


public class BuckBlock implements ASTBlock {

  private final BuckBlock myParent;
  private final Alignment myAlignment;
  private final Indent myIndent;
  private final PsiElement myPsiElement;
  private final ASTNode myNode;
  private final Wrap myWrap;
  private final Wrap myChildWrap;
  private final CodeStyleSettings mySettings;
  private final SpacingBuilder mySpacingBuilder;

  //private final BuckBlockContext myContext;
  private List<BuckBlock> mySubBlocks = null;
  private Alignment myChildAlignment;
  private final Alignment myDictAlignment;
  private final Alignment myPropertyValueAlignment;

  public BuckBlock(@Nullable final BuckBlock parent,
                   @NotNull final ASTNode node,
                   @NotNull CodeStyleSettings settings,
                   @Nullable final Alignment alignment,
                   @NotNull final Indent indent,
                   @Nullable final Wrap wrap) {
    myParent = parent;
    myAlignment = alignment;
    myIndent = indent;
    myNode = node;
    myPsiElement = node.getPsi();
    myWrap = wrap;
    myPropertyValueAlignment = Alignment.createAlignment(true);
    myDictAlignment = null;
    mySettings = settings;

    mySpacingBuilder = BuckFormattingModelBuilder.createSpacingBuilder(settings);

    if (myPsiElement instanceof BuckValueArray) {
      myChildWrap = Wrap.createWrap(CommonCodeStyleSettings.WRAP_ALWAYS, true);
    } else if (myPsiElement instanceof BuckRuleBody) {
      myChildWrap = Wrap.createWrap(CommonCodeStyleSettings.WRAP_ALWAYS, true);
    } else {
      myChildWrap = null;
    }
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

  private BuckBlock buildSubBlock(ASTNode childNode) {
    Indent indent = Indent.getNoneIndent();
    Alignment alignment = null;
    Wrap wrap = null;

    final TokenSet ALL_BRACES =
        TokenSet.orSet(TokenSet.create(BuckTypes.LBRACE), TokenSet.create(BuckTypes.RBRACE));

    if(hasElementType(myNode, TokenSet.create(BuckTypes.VALUE_ARRAY, BuckTypes.RULE_BODY))) {
      if (hasElementType(childNode, BuckTypes.COMMA)) {
        wrap = Wrap.createWrap(WrapType.NONE, true);
      } else if (!hasElementType(childNode, ALL_BRACES)) {
        assert myChildWrap != null;
        wrap = myChildWrap;
        indent = Indent.getNormalIndent();
      } else if (hasElementType(childNode, TokenSet.create(BuckTypes.LBRACE))) {
        if (myPsiElement instanceof BuckProperty) {
          assert myParent != null &&
              myParent.myParent != null &&
              myParent.myParent.myPropertyValueAlignment != null;
          alignment = myParent.myParent.myPropertyValueAlignment;
        }
      }
    } else if (hasElementType(myNode, BuckTypes.PROPERTY) ) {
      if (myPsiElement instanceof BuckProperty) {
        alignment = myParent.myPropertyValueAlignment;
      }
    }
    return new BuckBlock(this, childNode, mySettings, alignment, indent, wrap);
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
    return mySpacingBuilder.getSpacing(this, child1, child2);
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

  private BuckCodeStyleSettings getCustomSettings() {
    return mySettings.getCustomSettings(BuckCodeStyleSettings.class);
  }

}

