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
 * Echo.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.filter.Filter;
import com.github.fracpete.wekavirtualenv.command.filter.FilterChain;

/**
 * Just outputs a message.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Echo
  extends AbstractCommandWithOutputListeners
  implements CommandWithFilterSupport {

  /** for intercepting the process output. */
  protected FilterChain m_FilterChain;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_FilterChain = new FilterChain();
  }

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "echo";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Outputs the specified message.";
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
    result.addOption("--message")
      .dest("message")
      .help("the message to output. Use \\n, \\r and \\t for newline, carriage return and tab.")
      .required(true);
    result.addOption("--stderr")
      .dest("stderr")
      .help("for outputting the message on stderr instead of stdout")
      .argument(false);

    return result;
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
   * For outputting the line.
   *
   * @param line	the line to output
   * @param stdout	true if to output on stdout
   */
  protected void output(String line, boolean stdout) {
    if (stdout)
      System.out.println(line);
    else
      System.err.println(line);
    for (OutputListener l: m_OutputListeners)
      l.outputOccurred(line, stdout);
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns, String[] options) {
    String	line;
    boolean	stdout;

    line   = CommandUtils.unbackquote(ns.getString("message"));
    stdout = !ns.getBoolean("stderr");
    line   = m_FilterChain.intercept(line, stdout);
    if (line != null)
      output(line, stdout);
    return true;
  }
}
