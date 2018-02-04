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
 * Split.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;

/**
 * Splits a string variable into a string array variable.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Split
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "split";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Splits a string variable into a string array variable.";
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
    result.addOption("--str")
      .dest("str")
      .help("the string variable.")
      .required(true);
    result.addOption("--delimiter")
      .dest("delimiter")
      .help("the delimiter to split on")
      .required(true);
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
    String[] 	result;
    String	flat;

    value = getContext().getVariables().get(ns.getString("str"));
    if (value == null) {
      addError("Variable not present: " + ns.getString("str"));
      return false;
    }
    if (value instanceof String[]) {
      addError("Variable is already an array: " + ns.getString("str"));
      return false;
    }
    flat   = (String) value;
    result = flat.split(ns.getString("delimiter"));
    getContext().getVariables().set(ns.getString("dest"), result);

    return true;
  }
}
