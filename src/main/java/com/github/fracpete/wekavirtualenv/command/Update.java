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
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

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
  public ArgumentParser getParser() {
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
    result.addOption("--jvmparam")
      .dest("jvmparams")
      .multiple(true)
      .help("the JVM parameters to use");
    result.addOption("--no-jvmparams")
      .dest("nojvmparams")
      .help("if set, removes any existing JVM parameters")
      .argument(false);
    result.addOption("--weka")
      .dest("weka")
      .help("the full path to the weka.jar to use")
      .setDefault("");
    result.addOption("--envvar")
      .dest("envvar")
      .help("optional environment variables to set (key=value); override existing vars")
      .multiple(true);
    result.addOption("--no-envvars")
      .dest("noenvvars")
      .help("if set, removes any existing environment variables")
      .argument(false);
    result.addOption("--comment")
      .dest("comment")
      .help("optional comment string for the environment")
      .setDefault("");
    result.addOption("--pkg-mgr-offline")
      .dest("pkgmgroffline")
      .argument(false)
      .help("whether to run the package manager in offline mode")
      .setDefault(false);
    result.addOption("--quiet")
      .dest("quiet")
      .argument(false)
      .help("whether to suppress output when successfully finished")
      .setDefault(false);

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
    if (!ns.getList("jvmparams").isEmpty())
      newEnv.jvmparams = OptionUtils.joinOptions(ns.getList("jvmparams").toArray(new String[0]));
    if (ns.getBoolean("nojvmparams"))
      newEnv.jvmparams = null;
    if (!ns.getString("weka").isEmpty()) {
      file = new File(ns.getString("weka"));
      if (!file.exists())
        msg = "Weka jar does not exist: " + file;
      else
	newEnv.weka = ns.getString("weka");
    }
    newEnv.comment = ns.getString("comment");
    if (!ns.getList("envvar").isEmpty())
      newEnv.envvars = OptionUtils.joinOptions(ns.getList("envvar").toArray(new String[0]));
    if (ns.getBoolean("noenvvars"))
      newEnv.envvars = null;
    newEnv.pkgMgrOffline = ns.getBoolean("pkgmgroffline");

    // save setup
    if (msg == null)
      msg = Environments.update(newEnv);

    if (msg != null)
      addError(msg);
    else if (!ns.getBoolean("quiet"))
      println("Updated environment:\n" + newEnv, true);

    return (msg == null);
  }
}
