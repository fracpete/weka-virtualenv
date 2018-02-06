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
 * ArffViewer.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.command;

/**
 * Starts the ArffViewer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArffViewer
  extends AbstractGUICommand {

  /** the command. */
  protected com.github.fracpete.wekavirtualenv.command.ArffViewer m_Command;

  /**
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "ArffViewer";
  }

  /**
   * The group this action belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "gui";
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
    String	result;

    result    = null;
    m_Command = new com.github.fracpete.wekavirtualenv.command.ArffViewer();
    m_Command.setEnv(m_Environment);
    transferOutputListeners(m_Command);
    if (!m_Command.execute(new String[0])) {
      if (m_Command.hasErrors())
        result = m_Command.getErrors();
      else
        result = "Failed to launch ArffViewer!";
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
