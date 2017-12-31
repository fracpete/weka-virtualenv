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
 * Update.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;

import java.io.File;

/**
 * Allows adjusting of parameters of an existing environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Update
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "update";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Allows adjusting of parameters of an existing environment.";
  }

  /**
   * Returns whether it requires an environment.
   *
   * @return		true if required
   */
  @Override
  public boolean requiresEnvironment() {
    return true;
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		always null
   */
  @Override
  protected ArgumentParser getParser() {
    ArgumentParser 	result;

    result = new ArgumentParser(getName());
    result.addOption("--java")
      .dest("java")
      .help("the full path of the java binary to use for launching Weka\n"
        + "Use " + Environment.DEFAULT + " to reset to default")
      .setDefault("");
    result.addOption("--memory")
      .dest("memory")
      .help("the heap size to use for launching Weka (eg '1024m' or '2g')\n"
        + "Use " + Environment.DEFAULT + " to reset to default")
      .setDefault("");
    result.addOption("--weka")
      .dest("weka")
      .help("the full path to the weka.jar to use")
      .setDefault("");

    return result;
  }

  /**
   * Executes the command.
   *
   * @param ns 		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns, String[] options) {
    Environment 	newEnv;
    String		msg;
    File		file;

    msg    = null;
    newEnv = m_Env.clone();

    // overrides?
    if (!ns.getString("java").isEmpty()) {
      file = new File(ns.getString("java"));
      if (ns.getString("java").equals(Environment.DEFAULT) || file.isDirectory())
        newEnv.java = "";
      else
        newEnv.java = ns.getString("java");
    }
    else {
      newEnv.java = "";
    }
    if (ns.getString("memory").isEmpty() || ns.getString("memory").equals(Environment.DEFAULT))
      newEnv.memory = "";
    else
      newEnv.memory = ns.getString("memory");
    if (!ns.getString("weka").isEmpty()) {
      file = new File(ns.getString("weka"));
      if (!file.exists())
        msg = "Weka jar does not exist: " + file;
      else
	newEnv.weka = ns.getString("weka");
    }

    // save setup
    if (msg == null)
      msg = Environments.update(newEnv);

    if (msg != null)
      addError(msg);
    else
      System.out.println("Updated environment:\n" + newEnv);

    return (msg == null);
  }
}
