package com.intellij.plugin.buck.actions;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.GotoActionBase;
import com.intellij.ide.util.gotoByName.ChooseByNamePopup;
import com.intellij.ide.util.gotoByName.DefaultChooseByNameItemProvider;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.plugin.buck.actions.choosetargets.ChooseTargetItem;
import com.intellij.plugin.buck.actions.choosetargets.ChooseTargetModel;
import com.intellij.plugin.buck.build.BuckBuildTarget;
import com.intellij.plugin.buck.config.BuckSettingsProvider;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;

/**
 * Pop up a GUI for choose buck targets (alias)
 */
public class ChooseTargetAction extends GotoActionBase implements DumbAware {

  public static final String ACTION_TITLE = "Choose build target";
  public static final String ACTION_DESCRIPTION = "Choose Buck build target or alias";

  public ChooseTargetAction() {
    Presentation presentation = this.getTemplatePresentation();
    presentation.setText(ACTION_TITLE);
    presentation.setDescription(ACTION_DESCRIPTION);
    presentation.setIcon(AllIcons.Actions.Preview);
  }

  @Override
  protected void gotoActionPerformed(AnActionEvent e) {
    final Project project = e.getData(CommonDataKeys.PROJECT);
    if (project == null) {
      return;
    }

    final ChooseTargetModel model = new ChooseTargetModel(project);
    GotoActionCallback<BuckBuildTarget> callback = new GotoActionCallback<BuckBuildTarget>() {
      @Override
      public void elementChosen(ChooseByNamePopup chooseByNamePopup, Object element) {
        if (element == null) {
          return;
        }

        BuckSettingsProvider buckSettingsProvider = BuckSettingsProvider.getInstance();
        if (buckSettingsProvider == null || buckSettingsProvider.getState() == null) {
          return;
        }

        ChooseTargetItem item = (ChooseTargetItem) element;
        if (buckSettingsProvider.getState().lastAlias != null) {
          buckSettingsProvider.getState().lastAlias.put(
              project.getBasePath(), item.getBuildTarget());
        }
        BuckToolWindowFactory.updateBuckToolWindowTitle(project);
      }
    };

    DefaultChooseByNameItemProvider provider =
        new DefaultChooseByNameItemProvider(getPsiContext(e));
    showNavigationPopup(e, model, callback, "Choose Build Target", true, false, provider);
  }
}
