package com.intellij.plugin.buck.toolwindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.ComboBoxAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ChooseTargetAction extends ComboBoxAction {

    private Presentation myPresentation;

    public ChooseTargetAction() {
        getTemplatePresentation().setText("");
        getTemplatePresentation().setDescription("Choose build/install target");
        //getTemplatePresentation().setIcon(UIDesignerIcons.ChooseLocale);
    }

    @Override public JComponent createCustomComponent(Presentation presentation) {
        myPresentation = presentation;
        return super.createCustomComponent(presentation);
    }

    @NotNull
    @Override
    protected DefaultActionGroup createPopupActionGroup(JComponent button) {
        DefaultActionGroup group = new DefaultActionGroup();
        group.add(new SetTargetAction("target 1"));
        group.add(new SetTargetAction("target 2"));
        return group;
    }

    private class SetTargetAction extends AnAction {
        public SetTargetAction(String name) {
            super(name);
        }

        public void actionPerformed(AnActionEvent e) {
            myPresentation.setText(getTemplatePresentation().getText());
        }
    }
}
