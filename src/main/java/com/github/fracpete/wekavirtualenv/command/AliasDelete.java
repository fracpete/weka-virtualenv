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
 * AliasDelete.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.env.Aliases;

/**
 * Removes an alias definition.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AliasDelete
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "alias-del";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return
      "Removes an alias definition, i.e., shortcut for command and options.";
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
    result.addOption("--name")
      .dest("name")
      .help("the name of the alias definition")
      .required(true);
    result.addOption("--env")
      .dest("env")
      .help("the name of the environment, if environment-specific alias")
      .setDefault("")
      .required(false);

    return result;
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns, String[] options) {
    String	msg;

    // global?
    if (ns.getString("env").isEmpty()) {
      msg = Aliases.remove(null, ns.getString("name"));
      if (msg != null) {
	addError(msg);
	return false;
      }
      System.out.println("Successfully remove global alias: " + ns.getString("name"));
    }
    else {
      msg = Aliases.remove(ns.getString("env"), ns.getString("name"));
      if (msg != null) {
	addError(msg);
	return false;
      }
      System.out.println("Successfully removed alias for env=" + ns.getString("env") + ": " + ns.getString("name"));
    }

    return true;
  }
}
