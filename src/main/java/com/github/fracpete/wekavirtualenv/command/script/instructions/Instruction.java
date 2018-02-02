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
 * Instruction.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script.instructions;

/**
 * Generic interface for instructions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface Instruction {

  /** the line comment start. */
  public final static String COMMENT = "#";

  /**
   * For converting the the instruction into a string.
   *
   * @param indentation 	the number of indentation chars to use
   * @return			the generated string
   */
  public String toString(int indentation);
}
