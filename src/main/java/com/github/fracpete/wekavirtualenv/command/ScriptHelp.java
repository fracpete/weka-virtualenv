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
 * ScriptHelp.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.script.AbstractScriptCommand;
import com.github.fracpete.wekavirtualenv.command.script.ScriptCommand;

/**
 * Outputs help on the script commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ScriptHelp
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "script_help";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Prints help on the available script commands.";
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
    result.addOption("--cmd")
      .dest("cmd")
      .help("the specific script command to output the help for.")
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
    if (ns.getString("cmd").isEmpty()) {
      System.out.println("Available script commands:\n");
      for (ScriptCommand cmd : AbstractScriptCommand.getScriptCommands())
        System.out.println(cmd.generateHelpScreen(false, false));
    }
    else {
      ScriptCommand cmd = AbstractScriptCommand.getScriptCommand(ns.getString("cmd"));
      if (cmd == null) {
        System.err.println("Unknown script command: " + ns.getString("cmd"));
        return false;
      }
      System.out.println(cmd.getParser().generateHelpScreen(false));
    }
    System.out.println();
    System.out.println("Notes:");
    System.out.println("<options>");
    System.out.println("\tthe command supports additional options,");
    System.out.println("\tspecify the script's name to output detailed help.");
    System.out.println("<args>");
    System.out.println("\tthe command supports additional arguments");
    System.out.println("\tsee script's help.");

    return true;
  }
}
