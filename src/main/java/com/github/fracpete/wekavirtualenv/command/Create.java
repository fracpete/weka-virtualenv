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
 * Creates a new environment. Can be initialized with the content of an
 * existing 'wekafiles' directory.
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
    return
      "Creates a new environment.\n"
      + "Can be initialized with the content of an existing 'wekafiles' directory.";
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
    result.addOption("--name")
      .dest("name")
      .help("the name of the environment")
      .required(true);
    result.addOption("--java")
      .dest("java")
      .help("the full path of the java binary to use for launching Weka")
      .setDefault("");
    result.addOption("--memory")
      .dest("memory")
      .help("the heap size to use for launching Weka (eg '1024m' or '2g')")
      .setDefault("");
    result.addOption("--weka")
      .dest("weka")
      .help("the full path to the weka.jar to use")
      .required(true);
    result.addOption("--wekafiles")
      .dest("wekafiles")
      .help("the full path to the 'wekafiles' directory to initialize the environment with")
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
    Environment		env;
    String		msg;
    File		from;
    File		to;
    File		file;

    msg = null;

    env        = new Environment();
    env.name   = ns.getString("name");
    env.java   = ns.getString("java");
    env.memory = ns.getString("memory");
    env.weka   = ns.getString("weka");

    // check weka.jar
    file = new File(env.weka);
    if (!file.exists())
      msg = "Weka jar does not exist: " + file;
    else if (file.isDirectory())
      msg = "Weka jar points to a directory: " + file;

    // create env
    if (msg == null)
      msg = Environments.create(env);

    // copy wekafiles?
    if ((msg == null) && !ns.getString("wekafiles").isEmpty()) {
      from = new File(ns.getString("wekafiles"));
      to   = new File(Environments.getWekaFilesDir(env.name));
      if (!from.exists())
        msg = "'wekafiles' directory does not exist: " + from;
      else if (!from.isDirectory())
        msg = "'wekafiles' parameter does not point to a directory: " + from;
      if (msg == null) {
        try {
	  if (!FileUtils.copyOrMove(from, to, false, false))
	    msg = "Failed to copy directory '" + from + "' to '" + to + "'!";
	}
	catch (Exception e) {
          msg = "Failed to copy directory '" + from + "' to '" + to + "':\n" + e;
	}
      }
    }

    if (msg != null)
      addError("Failed to create environment:\n" + msg);
    else
      System.out.println("Created environment:\n\n" + env);

    return (msg == null);
  }
}
