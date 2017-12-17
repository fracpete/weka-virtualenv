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
 * Clone.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.action;

import com.github.fracpete.wekavirtualenv.core.FileUtils;
import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import com.github.fracpete.wekavirtualenv.parser.ArgumentParser;
import com.github.fracpete.wekavirtualenv.parser.Namespace;

import java.io.File;

/**
 * Clones a existing environment. Allows to adjust parameters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Clone
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "clone";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Clones an existing environment. Allows to adjust parameters.";
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
    result.addOption("--old")
      .name("old")
      .help("the name of the environment to clone")
      .required(true);
    result.addOption("--new")
      .name("new")
      .help("the name of the new environment")
      .required(true);
    result.addOption("--java")
      .name("java")
      .help("the full path of the java binary to use for launching Weka")
      .setDefault("");
    result.addOption("--memory")
      .name("memory")
      .help("the heap size to use for launching Weka (eg '1024m' or '2g')")
      .setDefault("");
    result.addOption("--weka")
      .name("weka")
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
    Environment 	oldEnv;
    Environment 	newEnv;
    String		msg;

    oldEnv = Environments.readEnv(ns.getString("old"));
    if (oldEnv == null) {
      System.err.println("Failed to load old environment: " + ns.getString("old"));
      return false;
    }

    newEnv = oldEnv.clone();
    newEnv.name = ns.getString("new");

    // overrides?
    if (!ns.getString("java").isEmpty())
      newEnv.java = ns.getString("java");
    if (!ns.getString("memory").isEmpty())
      newEnv.java = ns.getString("memory");
    if (!ns.getString("weka").isEmpty())
      newEnv.java = ns.getString("weka");

    // create empty environment
    msg = Environments.create(newEnv);
    if (msg != null)
      System.err.println("Failed to create environment:\n" + msg);
    else
      System.out.println("Created environment:\n\n" + newEnv);

    // copy "wekafiles" across
    if (msg == null) {
      try {
	if (!FileUtils.copyOrMove(
	  new File(Environments.getWekaFilesDir(oldEnv.name)),
	  new File(Environments.getWekaFilesDir(newEnv.name)),
	  false,
	  false)) {
	  msg = "Failed to copy 'wekafiles' from old to new environment:\n"
	    + "- old: " + Environments.getWekaFilesDir(oldEnv.name) + "\n"
	    + "- new: " + Environments.getWekaFilesDir(newEnv.name);
	}
      }
      catch (Exception e) {
	msg = "Failed to copy 'wekafiles' from old to new environment:\n"
	  + "- old: " + Environments.getWekaFilesDir(oldEnv.name) + "\n"
	  + "- new: " + Environments.getWekaFilesDir(newEnv.name) + "\n"
	  + "- exception:\n"
	  + e;
      }
      if (msg != null)
        System.err.println(msg);
    }

    return (msg == null);
  }
}
