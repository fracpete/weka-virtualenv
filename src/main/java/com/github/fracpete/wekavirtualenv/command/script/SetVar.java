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
 * SetVar.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.Namespace;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

/**
 * Sets a variable within a script.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SetVar
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "set";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Sets a variable in the form of 'name=value'.\n"
      + "The value can contain other variables, which will get evaluated in a lazy fashion.";
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
    String	pair;
    String	name;
    String	value;

    result = true;

    pair = OptionUtils.joinOptions(options);
    if (pair.contains("=")) {
      name  = pair.substring(0, pair.indexOf("=")).trim();
      value = pair.substring(pair.indexOf("=") + 1).trim();
      getContext().setVariable(name, value);
    }
    else {
      addError("Did not find expected 'name=value'!");
      result = false;
    }

    return result;
  }
}
