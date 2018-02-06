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
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import com.github.fracpete.wekavirtualenv.command.filter.Filter;
import com.github.fracpete.wekavirtualenv.command.filter.FilterChain;
import com.github.fracpete.wekavirtualenv.core.Destroyable;
import com.github.fracpete.wekavirtualenv.env.Environments;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Ancestor for commands that launch commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLaunchCommand
  extends AbstractCommandWithOutputListeners
  implements StreamingProcessOwner, CommandWithFilterSupport, Destroyable {

  /** the output. */
  protected StreamingProcessOutput m_Output;

  /** for intercepting the process output. */
  protected FilterChain m_FilterChain;

  /**
   * For initializing the members.
   */
  protected void initialize() {
    super.initialize();
    m_FilterChain = new FilterChain();
  }

  /**
   * Adds the filter.
   *
   * @param value	the filter to add
   */
  public void addFilter(Filter value) {
    m_FilterChain.addFilter(value);
  }

  /**
   * Returns whether it requires an environment.
   *
   * @return		true if required
   */
  public boolean requiresEnvironment() {
    return true;
  }

  /**
   * Returns whether the action is available.
   *
   * @return		true if available
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Returns the java command to use.
   *
   * @return		the java command
   */
  protected String getJava() {
    String	result;
    String	path;
    String	binary;

    if (!m_Env.java.isEmpty()) {
      result = m_Env.java;
    }
    else {
      path = System.getProperty("java.home") + File.separator + "bin" + File.separator;
      if (SystemUtils.IS_OS_WINDOWS)
        binary = "java.exe";
      else
        binary = "java";
      if (new File(path + binary).exists())
        result = path + binary;
      else
        result = binary;
    }

    return result;
  }

  /**
   * Builds the commands.
   *
   * @param cls		the class to launch
   * @param options	optional arguments for the class (null to ignore)
   * @return		the process builder
   */
  protected ProcessBuilder build(String cls, String[] options) {
    ProcessBuilder	result;
    List<String>	cmd;
    Map<String, String> vars;
    String[]		envvars;
    String[]		parts;

    cmd = new ArrayList<>();
    cmd.add(getJava());
    if (!m_Env.memory.isEmpty())
      cmd.add("-Xmx" + m_Env.memory);
    cmd.add("-classpath");
    cmd.add(m_Env.weka);
    cmd.add(cls);
    if (options != null)
      cmd.addAll(Arrays.asList(options));

    result = new ProcessBuilder();
    result.command(cmd);
    vars = result.environment();
    vars.put("WEKA_HOME", Environments.getWekaFilesDir(m_Env.name));
    if ((m_Env.envvars != null) && !m_Env.envvars.isEmpty()) {
      System.out.println("Using environment variables: " + m_Env.envvars);
      try {
	envvars = OptionUtils.splitOptions(m_Env.envvars);
	for (String envvar: envvars) {
	  parts = envvar.split("=");
	  if (parts.length == 2)
	    vars.put(parts[0], parts[1]);
	  else
	    System.err.println("Wrong format for environment variable (key=value)? " + envvar);
	}
      }
      catch (Exception e) {
        System.err.println("Failed to parse environment variables (blank separated list, key=value pairs): " + m_Env.envvars);
        e.printStackTrace();
      }
    }

    return result;
  }

  /**
   * Returns what output from the process to forward.
   *
   * @return 		the output type
   */
  public StreamingProcessOutputType getOutputType() {
    return StreamingProcessOutputType.BOTH;
  }

  /**
   * Processes the incoming line.
   *
   * @param line	the line to process
   * @param stdout	whether stdout or stderr
   */
  public synchronized void processOutput(String line, boolean stdout) {
    line = m_FilterChain.intercept(line, stdout);
    if (line != null) {
      if (stdout)
	System.out.println(line);
      else
	System.err.println(line);

      for (OutputListener l : m_OutputListeners)
	l.outputOccurred(line, stdout);
    }
  }

  /**
   * Launches the process.
   *
   * @param builder	the builder to use
   * @return		true if successful
   */
  protected boolean launch(ProcessBuilder builder) {
    try {
      m_Output = new StreamingProcessOutput(this);
      m_Output.monitor(builder);
      return true;
    }
    catch (Exception e) {
      addError("Failed to launch command:\n" + builder.command());
      return false;
    }
    finally {
      m_OutputListeners.clear();
    }
  }

  /**
   * Destroys the process if possible.
   */
  public void destroy() {
    if (m_Output != null)
      m_Output.destroy();
  }
}
