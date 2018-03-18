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
 * AbstractGUILaunchCommand.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.env.Environment;
import nz.ac.waikato.cms.core.Utils;

/**
 * Ancestor for commands that launch a GUI.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGUILaunchCommand
  extends AbstractLaunchCommand {

  /**
   * Returns the GUI classes to check.
   *
   * @return		the class names
   */
  protected abstract String[] getGUIClasses();

  /**
   * Returns whether the action is available.
   *
   * @return		true if available
   */
  @Override
  public boolean isAvailable() {
    boolean	result;

    result = false;

    for (String cls: getGUIClasses()) {
      result = Environment.hasClass(getEnv().weka, cls, true);
      if (result)
        break;
    }

    return result;
  }

  /**
   * Builds error message that command is not available.
   *
   * @return		the message
   */
  public String getNotAvailableMessage() {
    return "Not available in Weka " + getEnv().version() + ", "
      + "failed to locate class: " + Utils.flatten(getGUIClasses(), ", ");
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  protected boolean doExecute(Namespace ns, String[] options) {
    if (!isAvailable()) {
      addError(getNotAvailableMessage());
      return false;
    }

    for (String cls: getGUIClasses()) {
      if (Environment.hasClass(getEnv().weka, cls, false))
	return launch(build(cls, options));
    }

    addError("Failed to launch?");
    return false;
  }
}
