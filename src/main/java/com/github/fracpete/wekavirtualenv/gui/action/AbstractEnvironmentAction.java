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
 * AbstractEnvironmentAction.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.action;

import com.github.fracpete.wekavirtualenv.env.Environment;

/**
 * Ancestor for actions that work on an environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractEnvironmentAction
  extends AbstractAction {

  /** the environment. */
  protected Environment m_Environment;

  /**
   * Sets the environment to use.
   *
   * @param value	the environment
   */
  public void setEnvironment(Environment value) {
    m_Environment = value;
  }

  /**
   * Returns the environment in use.
   *
   * @return		the environment, null if none set
   */
  public Environment getEnvironment() {
    return m_Environment;
  }

  /**
   * Hook method for checking before executing the action.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String check() {
    String	result;

    result = super.check();

    if (result == null) {
      if (m_Environment == null)
        result = "No environment provided!";
    }

    return result;
  }
}
