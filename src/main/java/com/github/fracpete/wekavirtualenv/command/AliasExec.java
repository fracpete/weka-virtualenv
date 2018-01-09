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
 * AliasExec.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.env.Aliases;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Executes an alias definition.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AliasExec
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "alias-exec";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return
      "Executes an alias definition, i.e., shortcut for command and options.\n"
	+ "All options not consumed by this command will get used as additional "
	+ "options for the alias.\n"
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
    result.addOption("--inject-env")
      .dest("inject-env")
      .help("the name of the environment to inject into the alias command (when global alias)")
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
    List<String> 	errors;
    String[] 		cmd;
    List<String>	cmdList;
    CommandSetup results;

    options = compress(options);

    errors = new ArrayList<>();
    if (ns.getString("env").isEmpty()) {
      cmd = Aliases.get(null, ns.getString("name"), errors);
      if (!errors.isEmpty()) {
	for (String error: errors)
	  addError(error);
	return false;
      }
      if (cmd == null) {
	addError("Failed to retrieve global alias: " + ns.getString("name"));
	return false;
      }
    }
    else {
      cmd = Aliases.get(ns.getString("env"), ns.getString("name"), errors);
      if (!errors.isEmpty()) {
	for (String error: errors)
	  addError(error);
	return false;
      }
      if (cmd == null) {
	addError("Failed to retrieve alias (env=" + ns.getString("env") +"): " + ns.getString("name"));
	return false;
      }
    }

    cmdList = new ArrayList<>(Arrays.asList(cmd));

    // inject environment?
    if (!ns.getString("inject-env").isEmpty())
      cmdList.add(1, ns.getString("inject-env"));
    else if (!ns.getString("env").isEmpty())
      cmdList.add(1, ns.getString("env"));

    // append options
    cmdList.addAll(Arrays.asList(options));
    cmd = cmdList.toArray(new String[cmdList.size()]);

    // execute
    results = new CommandSetup();
    results.options = cmd;
    if (!configureSetup(results, false) || (results.command == null))
      return false;

    return results.command.execute(results.options);
  }
}
