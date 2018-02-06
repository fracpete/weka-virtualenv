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
 * AbstractScriptCommandWithOutputListeners.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.wekavirtualenv.command.OutputListener;
import com.github.fracpete.wekavirtualenv.command.OutputListenerSupporter;

import java.util.HashSet;
import java.util.Set;

/**
 * Interface for commands that support output listeners.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractScriptCommandWithOutputListeners
  extends AbstractScriptCommand
  implements OutputListenerSupporter {

  /** the output listeners. */
  protected Set<OutputListener> m_OutputListeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_OutputListeners = new HashSet<>();
  }

  /**
   * Adds the output listener.
   *
   * @param l		the listener
   */
  public void addOutputListener(OutputListener l) {
    m_OutputListeners.add(l);
  }

  /**
   * Removes the output listener.
   *
   * @param l		the listener
   */
  public void removeOutputListener(OutputListener l) {
    m_OutputListeners.remove(l);
  }
}
