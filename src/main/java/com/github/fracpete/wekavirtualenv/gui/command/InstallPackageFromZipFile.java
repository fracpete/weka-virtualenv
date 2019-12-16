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
 * InstallPackageFromZipFile.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.command;

import nz.ac.waikato.cms.gui.core.ApprovalDialog;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel.PropertyType;

import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Installs a package from a zip file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstallPackageFromZipFile
  extends AbstractGUICommand {

  /** the command. */
  protected com.github.fracpete.wekavirtualenv.command.PackageManager m_Command;

  /**
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Install package from ZIP file";
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

    panel.addPropertyType("zip", PropertyType.FILE_ABSOLUTE);
    panel.setLabel("zip", "ZIP file");
    panel.setHelp("zip", "The ZIP file of the package to install");

    panel.setPropertyOrder(new String[]{
      "zip",
    });

    props = new Properties();
    props.setProperty("zip", ".");
    panel.setProperties(props);
    if (GUIHelper.getParentDialog(getTabbedPane()) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(getTabbedPane()), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(getTabbedPane()), true);
    dialog.setDefaultCloseOperation(ApprovalDialog.DISPOSE_ON_CLOSE);
    dialog.setTitle("Install package from ZIP file");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return "User canceled dialog!";

    result    = null;
    props     = panel.getProperties();
    options   = new ArrayList<>();
    options.add("-install-package");
    options.add(props.getProperty("zip"));
    m_Command = new com.github.fracpete.wekavirtualenv.command.PackageManager();
    m_Command.setEnv(m_Environment);
    transferOutputListeners(m_Command);
    if (!m_Command.execute(options.toArray(new String[0]))) {
      if (m_Command.hasErrors())
        result = m_Command.getErrors();
      else
        result = "Failed to install package\n" + props.getProperty("zip");
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
