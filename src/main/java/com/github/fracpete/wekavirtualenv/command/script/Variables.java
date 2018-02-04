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
 * Variables.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages variables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Variables {

  /** the opening of a variable. */
  public final static String VAR_START = "${";

  /** the closing of a variable. */
  public final static String VAR_END = "}";

  /** the opening of an environment variable. */
  public final static String ENV_START = "@{";

  /** the closing of an environment variable. */
  public final static String ENV_END = "}";

  /** the variables. */
  protected Map<String,Object> m_Variables;

  /**
   * Initializes the variables.
   */
  public Variables() {
    m_Variables = new HashMap<>();
  }

  /**
   * Sets the variable and its value.
   *
   * @param name	the name of the variable
   * @param value	the value
   */
  public void set(String name, String value) {
    m_Variables.put(name, value);
  }

  /**
   * Sets the variable and its value.
   *
   * @param name	the name of the variable
   * @param value	the value
   */
  public void set(String name, String[] value) {
    m_Variables.put(name, value);
  }

  /**
   * Removes the specified variable.
   *
   * @param name	the name of the variable
   */
  public void remove(String name) {
    m_Variables.remove(name);
  }

  /**
   * Checks whether the variable exists.
   *
   * @param name	the variable to check
   * @return		true if present
   */
  public boolean has(String name) {
    return m_Variables.containsKey(name);
  }

  /**
   * Returns the value of the variable.
   *
   * @param name	the name of the variable to retrieve
   * @return		the value, null if variable doesn't exist
   */
  public Object get(String name) {
    return m_Variables.get(name);
  }

  /**
   * Returns the names of the currently stored variables.
   *
   * @return		the variable names
   */
  public List<String> names() {
    List<String>	result;

    result = new ArrayList<>(m_Variables.keySet());
    Collections.sort(result);

    return result;
  }

  /**
   * Expands all variables in the command.
   *
   * @param cmd		the command to process
   * @return		the processed command
   */
  public String expand(String cmd) {
    String	result;
    String	orig;
    Object	val;

    result = cmd;
    orig   = cmd;

    for (String name: m_Variables.keySet()) {
      val = m_Variables.get(name);
      if (val instanceof String)
	result = result.replace(VAR_START + name + VAR_END, (String) val);
    }

    // recursive expansion?
    if (result.contains(VAR_START) && !result.equals(orig))
      result = expand(result);

    for (String name: System.getenv().keySet()) {
      val    = System.getenv(name);
      result = result.replace(ENV_START + name + ENV_END, (String) val);
    }

    return result;
  }
}
