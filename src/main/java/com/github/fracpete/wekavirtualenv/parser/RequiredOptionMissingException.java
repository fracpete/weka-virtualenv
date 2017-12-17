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
 * RequiredOptionMissingException.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.parser;

import java.util.Collection;

/**
 * Gets thrown if required options weren't supplied.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RequiredOptionMissingException
  extends ArgumentParserException {

  /**
   * Initializes the exception.
   *
   * @param options	the required options that weren't supplied
   */
  public RequiredOptionMissingException(Collection<Option> options) {
    super("Required options not supplied: " + flatten(options));
  }

  /**
   * Flattens the options.
   *
   * @param options	the options to flatten
   * @return		the generated string
   */
  protected static String flatten(Collection<Option> options) {
    StringBuilder result;

    result = new StringBuilder();
    for (Option opt: options) {
      if (result.length() > 0)
	result.append(", ");
      result.append(opt.getFlag());
    }

    return result.toString();
  }
}
