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
 * Block.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script.instructions;

import com.github.fracpete.wekavirtualenv.core.InvalidIndentationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * Data structure for instructions, containing {@link Line}
 * and {@link Block}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Block
  extends ArrayList<Instruction>
  implements Instruction {

  /** the parent. */
  protected Block m_Parent;

  /** the indentation count. */
  protected int m_Indentation;

  /** the level. */
  protected Integer m_Level;

  /**
   * Default constructor.
   *
   * @param parent 		the parent block, null if outermost
   * @param indentation 	the indentation count
   */
  public Block(Block parent, int indentation) {
    super();
    m_Parent      = parent;
    m_Level       = null;
    m_Indentation = indentation;
  }

  /**
   * Initializes with the specified instructions.
   *
   * @param parent 		the parent block, null if outermost
   * @param indentation 	the indentation count
   * @param instructions	the instructions to use
   */
  public Block(Block parent, int indentation, Collection<Instruction> instructions) {
    super(instructions);
    m_Parent      = parent;
    m_Level       = null;
    m_Indentation = indentation;
  }

  /**
   * Returns the parent block, if any.
   *
   * @return		the parent, null if outermost
   */
  public Block getParent() {
    return m_Parent;
  }

  /**
   * Returns the indentation count (from the script code).
   * Used only for parsing.
   *
   * @return		the indentation code
   */
  public int getIndentation() {
    return m_Indentation;
  }

  /**
   * Returns the nesting level.
   *
   * @return		the nesting level, 0 for outermost
   */
  public int getLevel() {
    if (m_Level == null) {
      if (getParent() == null)
	m_Level = 0;
      else
	m_Level = getParent().getLevel() + 1;
    }
    return m_Level;
  }

  /**
   * For converting the the instruction into a string.
   *
   * @param indentation 	the nesting level
   * @return		the generated string
   */
  public String toString(int indentation) {
    StringBuilder	result;

    result = new StringBuilder();
    for (Instruction instruction: this) {
      if (instruction instanceof Block)
        result.append(instruction.toString());
      else
        result.append(instruction.toString(indentation));
    }

    return result.toString();
  }

  /**
   * Returns the instructions properly nested according to the nesting level.
   *
   * @return		the generated string
   */
  public String toString() {
    return toString(getIndentation());
  }

  /**
   * Removes all empty and comment lines.
   *
   * @param cmds	the commands to clean in-place
   */
  protected static void clean(List<String> cmds) {
    int		i;

    i = 0;
    while (i < cmds.size()) {
      if (cmds.get(i).trim().isEmpty()) {
        cmds.remove(i);
        continue;
      }
      if (cmds.get(i).trim().startsWith(COMMENT)) {
        cmds.remove(i);
        continue;
      }
      i++;
    }
  }

  /**
   * Combines lines that end with a backslash with the next one.
   *
   * @param cmds	the commands to combine
   */
  protected static void combine(List<String> cmds) {
    int		i;
    String	current;
    String	next;

    i = 0;
    while (i < cmds.size()) {
      if (cmds.get(i).trim().endsWith("\\")) {
        if (i < cmds.size() - 1) {
          current = cmds.get(i);
          next    = cmds.get(i + 1);
	  cmds.set(i, current.substring(0, current.length() - 1) + next);
	  cmds.remove(i + 1);
	  continue;
	}
      }
      i++;
    }
  }

  /**
   * Calculates the indentation level of the line.
   * Either tabs or blanks can be used (but not mixed).
   *
   * @param lineNo	the line number
   * @param line	the line to inspect
   * @return		the level
   * @throws InvalidIndentationException	if indentation mixes tabs/blanks
   */
  protected static int getLevel(int lineNo, String line) throws InvalidIndentationException {
    int		result;
    int		i;
    char	c;
    Boolean	tabs;

    result = 0;
    i      = 0;
    tabs   = null;
    while (i < line.length()) {
      c = line.charAt(i);

      // determine kind of indentation
      if (tabs == null) {
        if (c == '\t')
          tabs = true;
        else if (c == ' ')
          tabs = false;
      }

      // mixed indentation?
      if ((c == '\t') && !tabs)
        throw new InvalidIndentationException(lineNo, line);
      else if ((c == ' ') && tabs)
        throw new InvalidIndentationException(lineNo, line);

      if ((c == '\t') || (c == ' '))
        result++;
      else
        break;

      i++;
    }

    return result;
  }

  /**
   * Generates an instruction block from the list of instructions, automatically
   * nests blocks, if the instructions are indented.
   *
   * @param lines	the instructions to use
   * @return		the generated structure
   * @throws InvalidIndentationException	if indentation mixes tabs/blanks
   */
  public static Block parse(List<String> lines) throws InvalidIndentationException {
    Block 		result;
    Block 		inner;
    Stack<Block> 	nesting;
    int			i;
    int 		indentation;

    // preprocess
    clean(lines);
    combine(lines);

    result = new Block(null, 0);
    nesting = new Stack<>();
    nesting.push(result);
    for (i = 0; i < lines.size(); i++) {
      indentation = getLevel(i, lines.get(i));
      // level changed?
      if (indentation != nesting.peek().getIndentation()) {
        // finished inner block?
        if (indentation < nesting.peek().getIndentation()) {
	  nesting.pop();
	}
        else {
          inner = new Block(nesting.peek(), indentation);
          nesting.peek().add(inner);
	  nesting.push(inner);
	}
      }
      // add instruction
      nesting.peek().add(new Line(lines.get(i)));
    }

    return result;
  }
}
