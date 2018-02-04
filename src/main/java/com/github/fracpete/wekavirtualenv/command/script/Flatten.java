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
 * Flatten.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.CommandUtils;
import nz.ac.waikato.cms.core.Utils;

/**
 * Flattens an array variable into a single string variable.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Flatten
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "flatten";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Flattens an array variable into a single string variable.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		always null
   */
  @Override
  public ArgumentParser getParser() {
    ArgumentParser 	result;

    result = new ArgumentParser(getName());
    result.addOption("--array")
      .dest("array")
      .help("the array variable.")
      .required(true);
    result.addOption("--glue")
      .dest("glue")
      .help("the glue between the strings, use \\t, \\n or \\r for tab, newline or carriage return")
      .setDefault("");
    result.addOption("--dest")
      .dest("dest")
      .help("the name of the variable to store the result in.")
      .required(true);

    return result;
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
    Object	value;
    String	flat;

    value = getContext().getVariables().get(ns.getString("array"));
    if (value == null) {
      addError("Variable not present: " + ns.getString("array"));
      return false;
    }
    if (value instanceof String)
      flat = (String) value;
    else
      flat = Utils.flatten(
        (String[]) value,
	CommandUtils.unbackquote(ns.getString("glue")));
    getContext().getVariables().set(ns.getString("dest"), flat);

    return true;
  }
}
