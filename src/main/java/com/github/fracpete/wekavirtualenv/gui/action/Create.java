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
 * Create.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.action;

import nz.ac.waikato.cms.gui.core.ApprovalDialog;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel.PropertyType;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.io.File;
import java.util.Properties;

/**
 * Creates the environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Create
  extends AbstractAction {

  /** the command. */
  protected com.github.fracpete.wekavirtualenv.command.Create m_Command;

  /**
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Create";
  }

  /**
   * The group this action belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "admin";
  }

  /**
   * Returns whether the action generates console output.
   *
   * @return		true if the action generates console output
   */
  public boolean generatesOutput() {
    return false;
  }

  /**
   * Performs the actual execution.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    PropertiesParameterPanel  	panel;
    Properties			props;
    ApprovalDialog		dialog;
    String[]			options;
    File			file;

    panel = new PropertiesParameterPanel();

    panel.addPropertyType("name", PropertyType.STRING);
    panel.setLabel("name", "Environment name");
    panel.setHelp("name", "The name for the environment");

    panel.addPropertyType("java", PropertyType.FILE);
    panel.setLabel("java", "Java executable");
    panel.setHelp("java", "System default is used when pointing to a directory");

    panel.addPropertyType("memory", PropertyType.STRING);
    panel.setLabel("memory", "Heap size");
    panel.setHelp("memory", "System default is used when empty");

    panel.addPropertyType("weka", PropertyType.FILE);
    panel.setLabel("weka", "Weka jar");
    panel.setHelp("weka", "The weka jar to use for the environment, cannot be empty");

    panel.setPropertyOrder(new String[]{
      "name",
      "java",
      "memory",
      "weka",
    });

    props = new Properties();
    props.setProperty("name", "");
    props.setProperty("java", "");
    props.setProperty("memory", "");
    props.setProperty("weka", "");
    panel.setProperties(props);
    if (GUIHelper.getParentDialog(getAction().getTabbedPane()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getAction().getTabbedPane()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getAction().getTabbedPane()), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Enter parameters");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return "User canceled dialog!";

    result    = null;
    props     = panel.getProperties();
    file      = new File(props.getProperty("java"));
    if (file.isDirectory())
      props.setProperty("java", "");
    options   = new String[]{
      "--name", props.getProperty("name"),
      "--java", props.getProperty("java"),
      "--memory", props.getProperty("memory"),
      "--weka", props.getProperty("weka"),
    };
    m_Command = new com.github.fracpete.wekavirtualenv.command.Create();
    if (!m_Command.execute(options))
      result = "Failed to create environment!";
    m_Command = null;
    return result;
  }
}
