package com.intellij.plugin.buck.ui;

import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.plugin.buck.command.BuckCommandController;
import com.intellij.plugin.buck.command.BuckCommandUtils;

import javax.swing.*;
import java.awt.*;

//import com.intellij.plugin.buck.settings.BuckSettingsStorage;

//import com.intellij.plugin.buck.settings.BuckSettingsStorage;

public class ChooseProjectDialog extends DialogWrapper {
  private JTextField myField;
  private BuckCommandUtils.CommandType commandType;
  private Project project;

  public ChooseProjectDialog(Project project, BuckCommandUtils.CommandType type) {
    super(project, true);

    this.project = project;
    commandType = type;

    setTitle("Choose Project " + BuckCommandUtils.getPorjectDir(project));
    init();
  }

  protected void doOKAction() {
    String projectName = myField.getText();

    if (!projectName.isEmpty()) {
      //BuckSettingsStorage.addHistory(projectName);

      FileDocumentManager.getInstance().saveAllDocuments();

      BuckCommandUtils.sProject = project;
      new BuckCommandController(BuckCommandUtils.getPorjectDir(project), projectName)
          .executeBuckCommand(commandType);
    }

    super.doOKAction();
  }

  public JComponent getPreferredFocusedComponent() {
    return myField;
  }

  protected JComponent createCenterPanel() {
    return null;
  }

  protected JComponent createNorthPanel() {
    class MyTextField extends JTextField {
      public MyTextField() {
        super("");
      }

      public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(200, d.height);
      }
    }

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbConstraints = new GridBagConstraints();

    gbConstraints.insets = new Insets(4, 0, 8, 8);
    gbConstraints.fill = GridBagConstraints.VERTICAL;
    gbConstraints.weightx = 0;
    gbConstraints.weighty = 1;
    gbConstraints.anchor = GridBagConstraints.EAST;

    JLabel label = new JLabel("Project Alias");
    panel.add(label, gbConstraints);

    gbConstraints.fill = GridBagConstraints.BOTH;
    gbConstraints.weightx = 1;
    myField = new MyTextField();
    panel.add(myField, gbConstraints);
    myField.setToolTipText("Input the project name or alias here");

    return panel;
  }
}
