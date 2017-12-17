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
 * ListCommands.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.action;

import com.github.fracpete.wekavirtualenv.parser.Namespace;

/**
 * Lists all the commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ListCommands
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "list_cmds";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Lists all available commands.";
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
    System.out.println("Available commands:\n");
    for (AbstractCommand cmd: AbstractCommand.getCommands()) {
      System.out.println(cmd.getName() + (cmd.requiresEnvironment() ? " <env>" : "") + (cmd.getParser() != null ? " <options>" : ""));
      for (String line: cmd.getHelp().split("\n"))
	System.out.println("\t" + line);
      System.out.println();
    }
    System.out.println();
    System.out.println("Notes:");
    System.out.println("<env>");
    System.out.println("\tthe name of the environment to use for the command.");
    System.out.println("<options>");
    System.out.println("\tthe command supports additional options,");
    System.out.println("\tyou can use --help as argument to see further details.");

    return true;
  }
}
