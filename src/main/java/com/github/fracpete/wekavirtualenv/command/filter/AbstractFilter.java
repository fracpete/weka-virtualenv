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
 * AbstractFilter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.filter;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.CommandUtils;
import com.github.fracpete.wekavirtualenv.command.Help;
import nz.ac.waikato.cms.core.Utils;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFilter
  implements Filter {

  /** for storing any errors. */
  protected StringBuilder m_Errors;

  /** whether to capture stdout. */
  protected boolean m_StdOut;

  /** whether to capture stderr. */
  protected boolean m_StdErr;

  /**
   * Initializes the filter.
   */
  public AbstractFilter() {
    super();
    m_Errors = null;
  }

  /**
   * Generates a help screen.
   *
   * @param outputParser 	whether to output the help from the parser as well
   */
  public String generateHelpScreen(boolean requested, boolean outputParser) {
    StringBuilder	result;
    ArgumentParser parser;

    result = new StringBuilder();
    if (requested) {
      result.append("Help requested");
      result.append("\n\n");
    }

    result.append(getName()
      + (getParser() != null ? " <options>" : "") + "\n");

    for (String line: getHelp().split("\n"))
      result.append("\t").append(line).append("\n");

    parser = getParser();
    if (outputParser && (parser != null)) {
      result.append("\n");
      result.append(parser.generateHelpScreen(false, false, false, true));
    }

    return result.toString();
  }

  /**
   * Stores and outputs the error message.
   *
   * @param msg		the message
   * @param t 		the associated exception
   */
  public void addError(String msg, Throwable t) {
    addError(msg + "\n" + Utils.throwableToString(t));
  }

  /**
   * Stores and outputs the error message.
   *
   * @param msg		the message
   */
  public void addError(String msg) {
    if (m_Errors == null)
      m_Errors = new StringBuilder();
    else
      m_Errors.append("\n");
    m_Errors.append(msg);
  }

  /**
   * Returns whether any errors were recorded.
   *
   * @return		true if errors present
   */
  public boolean hasErrors() {
    return (m_Errors != null);
  }

  /**
   * Returns the errors.
   *
   * @return		the errors, null if none present
   */
  public String getErrors() {
    if (m_Errors == null)
      return null;
    else
      return m_Errors.toString();
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  public ArgumentParser getParser() {
    ArgumentParser 	result;

    result = new ArgumentParser(getName());
    result.addOption("--stdout")
      .dest("stdout")
      .help("for capturing output from stdout.")
      .argument(false);
    result.addOption("--stderr")
      .dest("stderr")
      .help("for capturing output from stderr.")
      .argument(false);

    return result;
  }

  /**
   * Initializes the filter with the parsed options.
   *
   * @param ns		the parsed options
   * @return		true if successfully parsed
   */
  public boolean initialize(Namespace ns) {
    m_StdOut = ns.getBoolean("stdout");
    m_StdErr = ns.getBoolean("stderr");
    return true;
  }

  /**
   * Intercepts the process output.
   *
   * @param line	the output to process
   * @param stdout	whether stdout or stderr
   * @return		the string to keep or null
   */
  protected abstract String doIntercept(String line, boolean stdout);

  /**
   * Intercepts the process output.
   *
   * @param line	the output to process
   * @param stdout	whether stdout or stderr
   * @return		the string to keep or null
   */
  public String intercept(String line, boolean stdout) {
    if ((m_StdOut && stdout) || (m_StdErr && !stdout))
      return doIntercept(line, stdout);
    else
      return line;
  }

  /**
   * Simply uses the filter for comparing.
   *
   * @param o		the other filter to compare with
   * @return		less than, equal to, or greater than zero
   * 			if the name is less than, equal to or greater
   */
  @Override
  public int compareTo(Filter o) {
    return getName().compareTo(o.getName());
  }

  /**
   * Checks whether the object is a filter and has the same name.
   *
   * @param obj		the object to compare with
   * @return		true if the same filter
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Filter) && (compareTo((Filter) obj) == 0);
  }

  /**
   * Configures the filter.
   *
   * @param setup	the setup to update
   * @return		the filter, null if failed to configure
   */
  public static boolean configure(FilterSetup setup) {
    Namespace 	ns;

    for (Filter f : getFilters()) {
      if (f.getName().equals(setup.options[0])) {
	setup.filter = f;
	break;
      }
    }
    if (setup.filter == null) {
      System.err.println("Unknown filter: " + setup.options[0]);
      new Help().execute(new String[0]);
      return false;
    }

    // remove filter from array
    setup.options[0] = "";
    setup.options = CommandUtils.compress(setup.options);

    try {
      ns = setup.filter.getParser().parseArgs(setup.options, true);
      return setup.filter.initialize(ns);
    }
    catch (Exception e) {
      System.err.println("Failed to parse options!");
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Lists all available filters.
   *
   * @return		the filters
   */
  public static List<Filter> getFilters() {
    List<Filter>	result;
    List<Class>		classes;
    Filter		cmd;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      Filter.class,
      new String[]{Filter.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
	cmd = (Filter) cls.newInstance();
	result.add(cmd);
      }
      catch (Exception e) {
	// ignored
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Returns the filter associated with the filter name.
   *
   * @param name	the name of the filter
   * @return		the filter, null if not available
   */
  public static Filter getFilter(String name) {
    Filter		result;

    result = null;
    for (Filter cmd: getFilters()) {
      if (cmd.getName().equals(name)) {
        result = cmd;
        break;
      }
    }

    return result;
  }
}
