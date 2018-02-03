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
 * ProcessOutputInterceptor.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

/**
 * Interface for intercepting the process output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface ProcessOutputInterceptor {

  /**
   * Intercepts the process output.
   *
   * @param line	the output to process
   * @param stdout	whether stdout or stderr
   * @return		true if to keep
   */
  public boolean intercept(String line, boolean stdout);
}
