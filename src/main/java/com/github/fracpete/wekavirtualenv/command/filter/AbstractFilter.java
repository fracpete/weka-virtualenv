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
      + (getParser() != null ? " <options>" : "")
      + (supportsAdditionalArguments() ? " <args>" : "") + "\n");

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
   */
  protected void addError(String msg) {
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
    return null;
  }

  /**
   * Returns whether the filter utilizes additional arguments that get passed on.
   *
   * @return		true if additional options
   */
  public boolean supportsAdditionalArguments() {
    return false;
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
