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
 * WekaVirtualEnv.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv;

import com.github.fracpete.wekavirtualenv.action.AbstractCommand;
import com.github.fracpete.wekavirtualenv.action.Help;

/**
 * Main class for launching commands and managing environments.
 * Use "help" to output a help screen.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaVirtualEnv {

  /**
   * Executes the commands.
   *
   * @param args	the commandline arguments
   * @throws Exception	if failed to parse or execute
   */
  public static void main(String[] args) throws Exception {
    AbstractCommand 	cmd;

    if (args.length == 0) {
      new Help().execute(new String[0]);
      System.exit(0);
    }

    cmd = null;
    for (AbstractCommand c: AbstractCommand.getCommands()) {
      if (c.getName().equals(args[0])) {
        cmd = c;
        break;
      }
    }
    if (cmd == null) {
      System.err.println("Unknown command: " + args[0]);
      new Help().execute(new String[0]);
      System.exit(1);
    }

    cmd.execute(args);
  }
}
