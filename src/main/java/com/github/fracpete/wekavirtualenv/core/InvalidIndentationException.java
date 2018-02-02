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
 * InvalidIndentationException.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

/**
 * Gets thrown if a mix of tabs and blanks is used.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InvalidIndentationException
  extends Exception {

  /**
   * Initializes the exception.
   *
   * @param lineNo	the line number (0-based)
   * @param line	the line itself
   */
  public InvalidIndentationException(int lineNo, String line) {
    super("Line " + (lineNo+1) + " mixes tabs and blanks for indentation: " + line);
  }
}
