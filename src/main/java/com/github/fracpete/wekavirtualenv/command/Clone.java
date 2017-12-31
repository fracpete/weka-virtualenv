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
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import nz.ac.waikato.cms.core.FileUtils;

import java.io.File;

/**
 * Clones a existing environment.
 * Allows adjusting of environment parameters
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
    return
      "Clones an existing environment.\n"
      + "Allows adjusting of environment parameters.";
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
      .dest("old")
      .help("the name of the environment to clone")
      .required(true);
    result.addOption("--new")
      .dest("new")
      .help("the name of the new environment")
      .required(true);
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
    result.addOption("--setup-only")
      .dest("setuponly")
      .help("if set, does not copy the 'wekafiles' directory of the environment")
      .argument(false);

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
    File		from;
    File		to;
    File		file;

    msg = null;

    oldEnv = Environments.readEnv(ns.getString("old"));
    if (oldEnv == null) {
      addError("Failed to load old environment: " + ns.getString("old"));
      return false;
    }

    newEnv = oldEnv.clone();
    newEnv.name = ns.getString("new");

    // overrides?
    if (!ns.getString("java").isEmpty()) {
      if (ns.getString("java").equals(Environment.DEFAULT))
        newEnv.java = "";
      else
        newEnv.java = ns.getString("java");
    }
    if (!ns.getString("memory").isEmpty()) {
      if (ns.getString("memory").equals(Environment.DEFAULT))
        newEnv.memory = "";
      else
        newEnv.memory = ns.getString("memory");
    }
    if (!ns.getString("weka").isEmpty()) {
      file = new File(ns.getString("weka"));
      if (!file.exists())
        msg = "Weka jar does not exist: " + file;
      else
	newEnv.weka = ns.getString("weka");
    }

    // create empty environment
    if (msg == null)
      msg = Environments.create(newEnv);

    // copy "wekafiles" across
    if (!ns.getBoolean("setuponly")) {
      if (msg == null) {
        from = new File(Environments.getWekaFilesDir(oldEnv.name));
        to = new File(Environments.getWekaFilesDir(newEnv.name));
        if (from.exists() && from.isDirectory()) {
          try {
            if (!FileUtils.copyOrMove(from, to, false, false)) {
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
        }
      }
    }

    if (msg != null)
      addError("Failed to create environment:\n" + msg);
    else
      System.out.println("Created environment:\n\n" + newEnv);

    return (msg == null);
  }
}
