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
 * Delete.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.action;

import com.github.fracpete.wekavirtualenv.core.Environments;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Deletes an existing environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Delete
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "delete";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Deletes an existing environment.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		always null
   */
  @Override
  protected ArgumentParser getParser() {
    ArgumentParser 	result;

    result = ArgumentParsers.newArgumentParser(getName());
    result.addArgument("-n", "--name")
      .dest("name")
      .help("the name of the environment to delete")
      .required(true);

    return result;
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns) {
    String	msg;

    msg = Environments.delete(ns.getString("name"));
    if (msg != null)
      System.err.println("Failed to delete environment '" + ns.getString("name") + "':\n" + msg);
    else
      System.out.println("Environment successfully deleted: " + ns.getString("name"));

    return (msg == null);
  }
}
