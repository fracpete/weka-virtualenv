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
 * Run.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.action;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.parser.ArgumentParser;
import com.github.fracpete.wekavirtualenv.parser.Namespace;

/**
 * Executes an arbitrary class.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Run
  extends AbstractLaunchCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "run";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Executes an arbitrary class with the left-over command-line options.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		always null
   */
  @Override
  protected ArgumentParser getParser() {
    ArgumentParser 	result;

    result = super.getParser();
    result.addOption("--class")
      .name("class")
      .help("the class to execute")
      .required(true);

    return result;
  }

  /**
   * Executes the command.
   *
   * @param env        the environment to use
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  protected boolean doExecute(Environment env, Namespace ns, String[] options) {
    return launch(build(env, ns.getString("class"), options));
  }
}
