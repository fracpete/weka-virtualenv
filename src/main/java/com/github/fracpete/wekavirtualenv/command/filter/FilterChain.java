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
 * FilterChain.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.filter;

import com.github.fracpete.wekavirtualenv.command.ProcessOutputInterceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * For applying multiple filters sequentially.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FilterChain
  implements ProcessOutputInterceptor {

  /** the filters to apply. */
  protected List<Filter> m_Filters;

  /**
   * Initializes the chain.
   */
  public FilterChain() {
    m_Filters = new ArrayList<>();
  }

  /**
   * Adds the filter to the chain.
   *
   * @param value	the filter to add
   */
  public void addFilter(Filter value) {
    m_Filters.add(value);
  }

  /**
   * Intercepts the process output.
   *
   * @param line	the output to process
   * @param stdout	whether stdout or stderr
   * @return		the string to keep or null
   */
  @Override
  public String intercept(String line, boolean stdout) {
    String	result;

    result = line;

    for (Filter filter: m_Filters) {
      result = filter.intercept(result, stdout);
      if (result == null)
        break;
    }

    return result;
  }
}
