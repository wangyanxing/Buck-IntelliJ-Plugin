package com.intellij.plugin.buck.actions.choosetargets;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import icons.BuckIcons;

import javax.swing.Icon;

public class ChooseTargetItem implements NavigationItem {
  private final String mAlias;
  private final String mTarget;
  private final BuckTargetItemPresentation mItemPresentation = new BuckTargetItemPresentation();

  public ChooseTargetItem(String alias, String target) {
    mAlias = alias;
    mTarget = target;
  }

  @Override
  public String getName() {
    return mAlias;
  }

  @Override
  public ItemPresentation getPresentation() {
    return mItemPresentation;
  }

  @Override
  public void navigate(boolean requestFocus) {
  }

  @Override
  public boolean canNavigate() {
    return false;
  }

  @Override
  public boolean canNavigateToSource() {
    return false;
  }

  private class BuckTargetItemPresentation implements ItemPresentation {
    @Override
    public String getPresentableText() {
      return mAlias;
    }

    @Override
    public String getLocationString() {
      return mTarget;
    }

    @Override
    public Icon getIcon(boolean b) {
      return BuckIcons.FILE_TYPE;
    }
  }
}
