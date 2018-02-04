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
 * CommandUtils.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class CommandUtils {

  /**
   * Removes any empty strings from the array.
   *
   * @param options 	the options to compress
   * @return		the compressed options
   */
  public static String[] compress(String[] options) {
    List<String> result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < options.length; i++) {
      if (!options[i].isEmpty())
	result.add(options[i]);
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Removes all options starting at the specified position.
   *
   * @param options	the options to process
   * @param pos		the position from which to remove arguments
   * @return		the updated options
   */
  public static String[] removeFrom(String[] options, int pos) {
    List<String>	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < options.length; i++) {
      if (i == pos)
        break;
      result.add(options[i]);
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Un-backquotes tab and newline and carriage return.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  public static String unbackquote(String s) {
    return OptionUtils.unbackQuoteChars(
      s,
      new String[]{"\\n" , "\\t", "\\r"},
      new char[]{'\n', '\t', '\r'});
  }
}
