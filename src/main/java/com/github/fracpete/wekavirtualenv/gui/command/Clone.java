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
 * Clone.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.command;

import nz.ac.waikato.cms.gui.core.ApprovalDialog;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel.PropertyType;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Clones the environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Clone
  extends AbstractGUICommand{

  /** the command. */
  protected com.github.fracpete.wekavirtualenv.command.Clone m_Command;

  /**
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Clone";
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
    List<String> 		options;
    File			file;
    String[]			envvars;

    panel = new PropertiesParameterPanel();

    panel.addPropertyType("newname", PropertyType.STRING);
    panel.setLabel("newname", "New name");
    panel.setHelp("newname", "The new name for the environment");

    panel.addPropertyType("java", PropertyType.FILE);
    panel.setLabel("java", "Java executable");
    panel.setHelp("java", "System default is used when pointing to a directory");

    panel.addPropertyType("memory", PropertyType.STRING);
    panel.setLabel("memory", "Heap size");
    panel.setHelp("memory", "System default is used when empty");

    panel.addPropertyType("weka", PropertyType.FILE);
    panel.setLabel("weka", "Weka jar");
    panel.setHelp("weka", "The weka jar to use for the environment, cannot be empty");

    panel.addPropertyType("envvars", PropertyType.STRING);
    panel.setLabel("envvars", "Environment variables");
    panel.setHelp("envvars", "Additional environment variables, blank-separated list of key=value pairs");

    panel.setPropertyOrder(new String[]{
      "newname",
      "java",
      "memory",
      "weka",
      "envvars",
    });

    props = new Properties();
    props.setProperty("newname", getEnvironment().name + ".clone");
    props.setProperty("java", getEnvironment().java);
    props.setProperty("memory", getEnvironment().memory);
    props.setProperty("weka", getEnvironment().weka);
    props.setProperty("envvars", getEnvironment().envvars);
    panel.setProperties(props);
    if (GUIHelper.getParentDialog(getTabbedPane()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getTabbedPane()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getTabbedPane()), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Enter clone parameters");
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

    options = new ArrayList<>();
    options.add("--old"); options.add(getEnvironment().name);
    options.add("--new"); options.add(props.getProperty("newname"));
    options.add("--java"); options.add(props.getProperty("java"));
    options.add("--memory"); options.add(props.getProperty("memory"));
    options.add("--weka"); options.add(props.getProperty("weka"));
    if (!props.getProperty("envvars", "").trim().isEmpty()) {
      try {
        envvars = OptionUtils.splitOptions(props.getProperty("envvars"));
        for (String envvar: envvars) {
	  options.add("--envvar");
	  options.add(envvar);
	}
      }
      catch (Exception e) {
        return "Failed to split blank-separated list of environment variables (key=value) pairs: " + e;
      }
    }
    else {
      options.add("--no-envvars");
    }
    m_Command = new com.github.fracpete.wekavirtualenv.command.Clone();
    if (!m_Command.execute(options.toArray(new String[options.size()]))) {
      if (m_Command.hasErrors())
        result = m_Command.getErrors();
      else
	result = "Failed to clone environment!";
    }
    m_Command = null;
    return result;
  }
}
