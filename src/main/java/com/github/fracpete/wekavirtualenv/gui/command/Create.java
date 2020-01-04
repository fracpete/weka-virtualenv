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
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
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
 * Creates the environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Create
  extends AbstractGUICommand {

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
   * Returns whether the action requires an environment.
   *
   * @return		true if the action requires an environment
   */
  @Override
  public boolean requiresEnvironment() {
    return false;
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

    panel.addPropertyType("envvars", PropertyType.STRING);
    panel.setLabel("envvars", "Environment variables");
    panel.setHelp("envvars", "Additional environment variables, blank-separated list of key=value pairs");

    panel.addPropertyType("comment", PropertyType.STRING);
    panel.setLabel("comment", "Comment");
    panel.setHelp("comment", "Optional comment for the environment");

    panel.addPropertyType("pkgmgroffline", PropertyType.BOOLEAN);
    panel.setLabel("pkgmgroffline", "PkgMgr offline");
    panel.setHelp("pkgmgroffline", "Whether to run the package manager in offline mode");

    panel.setPropertyOrder(new String[]{
      "name",
      "java",
      "memory",
      "weka",
      "envvars",
      "comment",
      "pkgmgroffline",
    });

    props = new Properties();
    props.setProperty("name", "");
    props.setProperty("java", "");
    props.setProperty("memory", "");
    props.setProperty("weka", "");
    props.setProperty("pkgmgroffline", "" + false);
    props.setProperty("envvars", "");
    props.setProperty("comment", "");
    panel.setProperties(props);
    if (GUIHelper.getParentDialog(getTabbedPane()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getTabbedPane()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getTabbedPane()), true);
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
    options = new ArrayList<>();
    options.add("--quiet");
    options.add("--name"); options.add(props.getProperty("name"));
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
    if (props.getProperty("pkgmgroffline").equalsIgnoreCase("true"))
      options.add("--pkg-mgr-offline");
    options.add("--comment"); options.add(props.getProperty("comment"));
    m_Command = new com.github.fracpete.wekavirtualenv.command.Create();
    if (!m_Command.execute(options.toArray(new String[options.size()]))) {
      if (m_Command.hasErrors())
        result = m_Command.getErrors();
      else
	result = "Failed to create environment!";
    }
    m_Command = null;
    return result;
  }
}
