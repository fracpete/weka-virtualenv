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
 * Line.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script.instructions;

/**
 * Contains a single instruction line.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Line
  implements Instruction {

  /** the instruction. */
  protected String m_Instruction;

  /**
   * Initializes with the instruction.
   *
   * @param instruction	the instruction to encapsulate
   */
  public Line(String instruction) {
    super();
    m_Instruction = instruction.trim();
  }

  /**
   * Returns the instruction.
   *
   * @return		the instruction
   */
  public String getInstruction() {
    return m_Instruction;
  }

  /**
   * For converting the the instruction into a string.
   *
   * @param indentation 	the nesting level
   * @return		the generated string
   */
  public String toString(int indentation) {
    StringBuilder	result;
    int			i;

    result = new StringBuilder();
    for (i = 0; i < indentation; i++)
      result.append(" ");
    result.append(m_Instruction);
    result.append("\n");

    return result.toString();
  }

  /**
   * Returns the instruction by itself.
   *
   * @return		the instruction
   */
  public String toString() {
    return m_Instruction;
  }
}
