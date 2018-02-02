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
 * UnsetVar.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.Namespace;

/**
 * Removes a variable within a script.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class UnsetVar
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "unset";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Removes the specified variable.";
  }

  /**
   * Returns whether the command utilizes additional arguments that get passed on.
   *
   * @return		true if additional options
   */
  @Override
  public boolean supportsAdditionalArguments() {
    return true;
  }

  /**
   * Evaluates the script command.
   *
   * @param ns		the namespace
   * @param options	the options
   * @return		true if successful
   */
  @Override
  protected boolean evalCommand(Namespace ns, String[] options) {
    boolean	result;

    result = true;

    if (options.length == 1) {
      getContext().getVariables().remove(options[0]);
    }
    else {
      addError("Missing name of variable to remove!");
      result = false;
    }

    return result;
  }
}
