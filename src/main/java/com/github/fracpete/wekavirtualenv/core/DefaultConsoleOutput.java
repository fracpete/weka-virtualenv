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
 * DefaultConsoleOutput.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

import nz.ac.waikato.cms.core.Utils;

import java.io.Serializable;

/**
 * For redirecting output into the console.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultConsoleOutput
  implements Serializable, ConsoleOutputSupporter {

  /**
   * Outputs the specified string to either stdout or stderr.
   *
   * @param line	the line to output
   * @param stdout	whether to output on stdout or stderr
   */
  @Override
  public void println(String line, boolean stdout) {
    if (stdout)
      System.out.println(line);
    else
      System.err.println(line);
  }

  /**
   * Outputs the specified message on stderr.
   *
   * @param msg		the message to output
   * @param t 		the exception
   */
  @Override
  public void println(String msg, Throwable t) {
    System.err.println(msg + "\n" + Utils.throwableToString(t));
  }
}
