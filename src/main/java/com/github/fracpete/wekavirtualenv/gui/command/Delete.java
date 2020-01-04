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
 * Delete.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.command;

import nz.ac.waikato.cms.gui.core.GUIHelper;

import javax.swing.JOptionPane;

/**
 * Deletes the environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Delete
  extends AbstractGUICommand {

  /** the command. */
  protected com.github.fracpete.wekavirtualenv.command.Delete m_Command;

  /**
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Delete";
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
    String	result;
    int		retVal;

    retVal = JOptionPane.showConfirmDialog(
      GUIHelper.getParentComponent(getEnvironmentsPanel()),
      "Delete environment '" + getEnvironment().name + "'?");
    if (retVal != JOptionPane.OK_OPTION)
      return null;

    result    = null;
    m_Command = new com.github.fracpete.wekavirtualenv.command.Delete();
    if (!m_Command.execute(new String[]{"--name", getEnvironment().name, "--quiet"})) {
      if (m_Command.hasErrors())
        result = m_Command.getErrors();
      else
	result = "Failed to delete environment!";
    }
    m_Command = null;
    return result;
  }
}
