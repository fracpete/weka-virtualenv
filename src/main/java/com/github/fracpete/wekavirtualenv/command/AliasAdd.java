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
 * AliasAdd.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.env.Aliases;

/**
 * Adds an alias definition, i.e., shortcut for command and options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AliasAdd
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "alias-add";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return
      "Adds an alias definition, i.e., shortcut for command and options.\n"
	+ "All options not consumed by this command will get used as options "
	+ "for the alias.\n"
	+ "No checks are being performed on the correctness.";
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
   * Returns whether the command utilizes additional arguments that get passed on.
   *
   * @return		true if additional options
   */
  @Override
  public boolean supportsAdditionalArguments() {
    return true;
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

    options = compress(options);

    // global?
    if (ns.getString("env").isEmpty()) {
      msg = Aliases.add(null, ns.getString("name"), options);
      if (msg != null) {
	addError(msg);
	return false;
      }
      System.out.println("Successfully added global alias: " + ns.getString("name"));
    }
    else {
      msg = Aliases.add(ns.getString("env"), ns.getString("name"), options);
      if (msg != null) {
	addError(msg);
	return false;
      }
      System.out.println("Successfully added alias for env=" + ns.getString("env") + ": " + ns.getString("name"));
    }

    return true;
  }
}
