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

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.processoutput4j.core.StreamingProcessOutputType;
import com.github.fracpete.processoutput4j.core.StreamingProcessOwner;
import com.github.fracpete.processoutput4j.output.StreamingProcessOutput;
import com.github.fracpete.wekavirtualenv.env.Environments;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ancestor for commands that launch commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLaunchCommand
  extends AbstractCommand
  implements StreamingProcessOwner {

  /** the output listeners. */
  protected Set<OutputListener> m_OutputListeners;

  /** the output. */
  protected StreamingProcessOutput m_Output;

  /**
   * Initializes the command.
   */
  public AbstractLaunchCommand() {
    m_OutputListeners = new HashSet<>();
  }

  /**
   * Adds the output listener.
   *
   * @param l		the listener
   */
  public void addOutputListener(OutputListener l) {
    m_OutputListeners.add(l);
  }

  /**
   * Removes the output listener.
   *
   * @param l		the listener
   */
  public void removeOutputListener(OutputListener l) {
    m_OutputListeners.remove(l);
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
    if (stdout)
      System.out.println(line);
    else
      System.err.println(line);

    for (OutputListener l: m_OutputListeners)
      l.outputOccurred(line, stdout);
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
      System.err.println("Failed to launch command:\n" + builder.command());
      return false;
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
