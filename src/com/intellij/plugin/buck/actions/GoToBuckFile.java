package com.intellij.plugin.buck.actions;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.plugin.buck.build.BuckBuildManager;
import com.intellij.plugin.buck.build.BuckBuildUtil;
import com.intellij.plugin.buck.ui.BuckToolWindowFactory;
import com.intellij.pom.Navigatable;

/**
 * Go to its BUCK file for current source file
 */
public class GoToBuckFile extends AnAction {

  public GoToBuckFile() {
    super("Go to Buck file", "Open the buck file of current file", null);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final Project project = e.getProject();
    if (project == null) {
      return;
    }
    if (!BuckBuildManager.getInstance(e.getProject()).isBuckProject(project)) {
      BuckToolWindowFactory.outputConsoleMessage(
          project,
          BuckBuildManager.NOT_BUCK_PROJECT_ERROR_MESSAGE,
          ConsoleViewContentType.ERROR_OUTPUT);
      return;
    }

    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
    if (editor == null) {
      return;
    }
    final Document document = editor.getDocument();
    if (document == null) {
      return;
    }
    VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
    if (virtualFile == null) {
      return;
    }

    VirtualFile parent = virtualFile.getParent();
    if (parent == null) {
      return;
    }

    VirtualFile buckFile = BuckBuildUtil.getBuckFileFromDirectory(parent);

    final VirtualFile file = buckFile;
    if (file != null) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        @Override
        public void run() {
          //this is for better cursor position
          OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
          Navigatable n = descriptor.setUseCurrentWindow(false);
          if (!n.canNavigate()) {
            return;
          }
          n.navigate(true);
        }
      }, ModalityState.NON_MODAL);
    }
  }
}
