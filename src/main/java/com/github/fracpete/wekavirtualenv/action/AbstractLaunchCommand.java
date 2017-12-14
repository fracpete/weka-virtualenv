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
 * AbstractLaunchCommand.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.action;

import com.github.fracpete.processoutput4j.output.ConsoleOutputProcessOutput;
import com.github.fracpete.wekavirtualenv.core.Environment;
import com.github.fracpete.wekavirtualenv.core.Environments;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import org.apache.commons.lang3.SystemUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for commands that.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLaunchCommand
  extends AbstractCommand {

  /**
   * Returns the java command to use.
   *
   * @param env		the environment to use
   * @return		the java command
   */
  protected String getJava(Environment env) {
    String	result;

    if (!env.java.isEmpty()) {
      result = env.java;
    }
    else {
      // TODO use the one used for stating this process?
      if (SystemUtils.IS_OS_WINDOWS)
        result = "java.exe";
      else
        result = "java";
    }

    return result;
  }

  /**
   * Builds the commands.
   *
   * @param env		the environment to use
   * @param cls		the class to launch
   * @param options	optional arguments for the class (null to ignore)
   * @return		the process builder
   */
  protected ProcessBuilder build(Environment env, String cls, String[] options) {
    ProcessBuilder	result;
    List<String>	cmd;

    cmd = new ArrayList<>();
    cmd.add(getJava(env));
    if (!env.memory.isEmpty())
      cmd.add("-Xmx" + env.memory);
    cmd.add("-classpath");
    cmd.add(env.weka);
    cmd.add(cls);
    if (options != null)
      cmd.addAll(Arrays.asList(options));

    result = new ProcessBuilder();
    result.command(cmd);

    return result;
  }

  /**
   * Launches the process.
   *
   * @param builder	the builder to use
   * @return		true if successful
   */
  protected boolean launch(ProcessBuilder builder) {
    try {
      ConsoleOutputProcessOutput output = new ConsoleOutputProcessOutput();
      output.monitor(builder);
      return true;
    }
    catch (Exception e) {
      System.err.println("Failed to launch command:\n" + builder.command());
      return false;
    }
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
      .help("the name of the environment to use")
      .required(true);

    return result;
  }

  /**
   * Executes the command.
   *
   * @param env 	the environment to use
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @return		true if successful
   */
  protected abstract boolean doExecute(Environment env, Namespace ns);

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns) {
    Environment		env;

    env = Environments.readEnv(ns.getString("name"));
    if (env == null) {
      System.err.println("Environment '" + ns.getString("name") + "' does not exist or failed to read!");
      return false;
    }

    return doExecute(env, ns);
  }
}
