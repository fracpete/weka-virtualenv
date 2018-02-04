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
 * Replace.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;

/**
 * Performs string replacement, simple or regular expression based.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Replace
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "replace";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Performs string replacement, simple or regular expression based.\n"
      + "If no replacement string is provided the empty string is used.";
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
      .help("the string or string array variable to process.")
      .required(true);
    result.addOption("--find")
      .dest("find")
      .help("the string or pattern to find.")
      .required(true);
    result.addOption("--replace")
      .dest("replace")
      .help("the replacement string to use.")
      .setDefault("");
    result.addOption("--regexp")
      .dest("regexp")
      .help("whether to use regular expression matching.")
      .argument(false);
    result.addOption("--all")
      .dest("all")
      .help("whether to replace all occurrences in case of regexp matching.")
      .argument(false);
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
    String 	find;
    String 	replace;
    boolean 	regExp;
    boolean 	all;
    Object	value;
    String[]	lines;
    boolean	array;
    String[]	result;
    int		i;

    value = getContext().getVariables().get(ns.getString("str"));
    array = (value instanceof String[]);
    if (!array)
      lines = new String[]{(String) value};
    else
      lines = (String[]) value;
    find    = ns.getString("find");
    replace = ns.getString("replace");
    regExp  = ns.getBoolean("regexp");
    all     = regExp && ns.getBoolean("all");

    result = new String[lines.length];
    for (i = 0; i < lines.length; i++) {
      if (regExp) {
	if (all)
	  result[i] = lines[i].replaceAll(find, replace);
	else
	  result[i] = lines[i].replaceFirst(find, replace);
      }
      else {
	result[i] = lines[i].replace(find, replace);
      }
    }

    if (array)
      getContext().getVariables().set(ns.getString("dest"), result);
    else
      getContext().getVariables().set(ns.getString("dest"), result[0]);

    return true;
  }
}
