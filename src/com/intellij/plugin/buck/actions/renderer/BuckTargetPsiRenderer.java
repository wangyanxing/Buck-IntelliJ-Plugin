package com.intellij.plugin.buck.actions.renderer;

import com.intellij.ide.util.PlatformModuleRendererFactory;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;
import java.awt.*;

public class BuckTargetPsiRenderer extends PsiElementListCellRenderer<PsiElement> {

  private String mAlias;
  private String mTarget;

  public BuckTargetPsiRenderer() {
    setFocusBorderEnabled(false);
  }

  @Override
  public String getElementText(PsiElement element) {
    return mAlias == null ? "" : mAlias;
  }

  @Override
  protected String getContainerText(PsiElement element, String name) {
    return mTarget == null ? "" : "(" + mTarget + ")";
  }

  @Override
  protected boolean customizeNonPsiElementLeftRenderer(
      ColoredListCellRenderer renderer,
      JList list,
      Object value,
      int index,
      boolean selected,
      boolean hasFocus) {
    if (!(value instanceof NavigationItem)) {
      return false;
    }

    NavigationItem item = (NavigationItem)value;
    TextAttributes attributes = getNavigationItemAttributes(item);

    SimpleTextAttributes nameAttributes =
        attributes != null ? SimpleTextAttributes.fromTextAttributes(attributes) : null;

    Color color = list.getForeground();
    if (nameAttributes == null) {
      nameAttributes = new SimpleTextAttributes(Font.PLAIN, color);
    }
    renderer.append(item + " ", nameAttributes);
    ItemPresentation itemPresentation = item.getPresentation();
    assert itemPresentation != null;
    renderer.setIcon(itemPresentation.getIcon(true));

    String locationString = itemPresentation.getLocationString();
    if (!StringUtil.isEmpty(locationString)) {
      renderer.append(locationString, new SimpleTextAttributes(Font.PLAIN, JBColor.GRAY));
    }
    return true;
  }

  @Override
  protected DefaultListCellRenderer getRightCellRenderer(final Object value) {
    final DefaultListCellRenderer rightRenderer = super.getRightCellRenderer(value);
    if (rightRenderer instanceof PlatformModuleRendererFactory.PlatformModuleRenderer) {
      // that renderer will display file path, but we're showing it ourselves
      // no need to show twice
      return null;
    }
    return rightRenderer;
  }

  @Override
  protected int getIconFlags() {
    return Iconable.ICON_FLAG_READ_STATUS;
  }

  public void setTarget(String target) {
    this.mTarget = target;
  }

  public void setAlias(String alias) {
    this.mAlias = alias;
  }
}
