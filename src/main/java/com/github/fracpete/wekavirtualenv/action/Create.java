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
 * Create.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.action;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Creates a new environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Create
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "create";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Creates a new environment.";
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
      .help("the name of the environment")
      .required(true);
    result.addArgument("-j", "--java")
      .dest("java")
      .help("the full path of the java binary to use for launching Weka")
      .setDefault("");
    result.addArgument("-m", "--memory")
      .dest("memory")
      .help("the heap size to use for launching Weka (eg '1024m' or '2g')")
      .setDefault("");
    result.addArgument("-w", "--weka")
      .dest("weka")
      .help("the full path to the weka.jar to use")
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
    Environment		env;
    String		msg;

    env        = new Environment();
    env.name   = ns.getString("name");
    env.java   = ns.getString("java");
    env.memory = ns.getString("memory");
    env.weka   = ns.getString("weka");

    msg = Environments.create(env);
    if (msg != null)
      System.err.println("Failed to create environment:\n" + msg);
    else
      System.out.println("Created environment:\n\n" + env);

    return (msg == null);
  }
}
