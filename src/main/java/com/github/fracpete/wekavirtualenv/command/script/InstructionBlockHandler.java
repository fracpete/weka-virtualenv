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
 * InstructionBlockHandler.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.wekavirtualenv.command.Command;
import com.github.fracpete.wekavirtualenv.command.script.instructions.Block;

/**
 * Interface for commands that handle a block of instructions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface InstructionBlockHandler
  extends Command, VariablesHandler {

  /**
   * Sets the instructions.
   *
   * @param instructions	the commands to execute
   */
  public void setInstructions(Block instructions);
}
