/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Run.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.command;

import nz.ac.waikato.cms.gui.core.ApprovalDialog;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel.PropertyType;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Executes a class with parameters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Run
  extends AbstractGUICommand {

  /** the command. */
  protected com.github.fracpete.wekavirtualenv.command.Run m_Command;

  /**
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Run";
  }

  /**
   * The group this action belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "run";
  }

  /**
   * Returns whether the action requires an environment.
   *
   * @return		true if the action requires an environment
   */
  @Override
  public boolean requiresEnvironment() {
    return true;
  }

  /**
   * Returns whether the action generates console output.
   *
   * @return		true if the action generates console output
   */
  public boolean generatesOutput() {
    return true;
  }

  /**
   * Performs the actual execution.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    PropertiesParameterPanel 	panel;
    Properties 			props;
    ApprovalDialog 		dialog;
    List<String> 		options;

    panel = new PropertiesParameterPanel();
    panel.setButtonPanelVisible(true);

    panel.addPropertyType("class", PropertyType.STRING);
    panel.setLabel("class", "Class");
    panel.setHelp("class", "The class to execute, e.g., weka.classifiers.trees.J48");

    panel.addPropertyType("options", PropertyType.STRING);
    panel.setLabel("options", "Options");
    panel.setHelp("options", "The options to provide to the class");

    panel.setPropertyOrder(new String[]{
      "class",
      "options",
    });

    props = new Properties();
    props.setProperty("class", "weka.classifiers.trees.J48");
    props.setProperty("options", "");
    panel.setProperties(props);
    if (GUIHelper.getParentDialog(getTabbedPane()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getTabbedPane()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getTabbedPane()), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Execute class");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return "User canceled dialog!";

    result    = null;
    props     = panel.getProperties();
    options   = new ArrayList<>();
    options.add("--class");
    options.add(props.getProperty("class"));
    if (!props.getProperty("options").trim().isEmpty()) {
      try {
	options.addAll(Arrays.asList(OptionUtils.splitOptions(props.getProperty("options"))));
      }
      catch (Exception e) {
        return "Failed to parse options: " + props.getProperty("options") + "\n" + e;
      }
    }

    m_Command = new com.github.fracpete.wekavirtualenv.command.Run();
    m_Command.setEnv(m_Environment);
    transferOutputListeners(m_Command);
    if (!m_Command.execute(options.toArray(new String[options.size()]))) {
      if (m_Command.hasErrors())
        result = m_Command.getErrors();
      else
        result = "Failed to execute class!";
    }
    m_Command = null;
    return result;
  }

  /**
   * Destroys the process if possible.
   */
  public void destroy() {
    if (m_Command != null)
      m_Command.destroy();
  }
}
