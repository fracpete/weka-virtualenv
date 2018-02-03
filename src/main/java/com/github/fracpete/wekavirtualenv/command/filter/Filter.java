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
 * Filter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.filter;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.ErrorHandler;
import com.github.fracpete.wekavirtualenv.command.ProcessOutputInterceptor;

/**
 * Interface for string filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface Filter
  extends Comparable<Filter>, ErrorHandler, ProcessOutputInterceptor {

  /**
   * The name of the filter (used on the filterline).
   *
   * @return		the name
   */
  public String getName();

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp();

  /**
   * Generates a help screen.
   *
   * @param outputParser 	whether to output the help from the parser as well
   */
  public String generateHelpScreen(boolean requested, boolean outputParser);

  /**
   * Returns whether any errors were recorded.
   *
   * @return		true if errors present
   */
  public boolean hasErrors();

  /**
   * Returns the errors.
   *
   * @return		the errors, null if none present
   */
  public String getErrors();

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  public ArgumentParser getParser();

  /**
   * Initializes the filter with the parsed options.
   *
   * @param ns		the parsed options
   * @return		true if successfully parsed
   */
  public boolean initialize(Namespace ns);

  /**
   * Simply uses the filter for comparing.
   *
   * @param o		the other filter to compare with
   * @return		less than, equal to, or greater than zero
   * 			if the name is less than, equal to or greater
   */
  @Override
  public int compareTo(Filter o);

  /**
   * Checks whether the object is a filter and has the same name.
   *
   * @param obj		the object to compare with
   * @return		true if the same filter
   */
  @Override
  public boolean equals(Object obj);
}
