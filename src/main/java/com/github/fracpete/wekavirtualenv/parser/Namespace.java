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
 * Namespace.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * For storing parsed options.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Namespace
  implements Serializable {

  /** for storing the options (name - value). */
  protected Map<String,String> m_Stored;

  /**
   * Initializes the namespace.
   */
  public Namespace() {
    m_Stored = new HashMap<>();
  }

  /**
   * Sets the default value for the named option.
   *
   * @param name	the name
   * @param value	the default value
   */
  public void setDefault(String name, String value) {
    m_Stored.put(name, value);
  }

  /**
   * Sets the default value for the named option.
   *
   * @param name	the name
   * @param value	the default value
   */
  public void setDefault(String name, boolean value) {
    m_Stored.put(name, "" + value);
  }

  /**
   * Sets the default value for the named option.
   *
   * @param name	the name
   * @param value	the default value
   */
  public void setDefault(String name, int value) {
    m_Stored.put(name, "" + value);
  }

  /**
   * Sets the default value for the named option.
   *
   * @param name	the name
   * @param value	the default value
   */
  public void setDefault(String name, float value) {
    m_Stored.put(name, "" + value);
  }

  /**
   * Sets the "parsed" value for the named option.
   *
   * @param name	the name
   * @param value	the "parsed" value
   */
  public void setValue(String name, String value) {
    m_Stored.put(name, value);
  }

  /**
   * Flips the "parsed" boolean value for the named option.
   *
   * @param name	the name
   */
  public void flipValue(String name) {
    if (m_Stored.get(name).equals("true"))
      m_Stored.put(name, "" + false);
    else
      m_Stored.put(name, "" + true);
  }

  /**
   * Returns the string value associated with an option name.
   *
   * @param name	the name
   * @return		the associated value
   */
  public String getString(String name) {
    return m_Stored.get(name);
  }

  /**
   * Returns the boolean value associated with an option name.
   *
   * @param name	the name
   * @return		the associated value
   */
  public boolean getBoolean(String name) {
    return Boolean.parseBoolean(m_Stored.get(name));
  }

  /**
   * Returns the int value associated with an option name.
   *
   * @param name	the name
   * @return		the associated value
   */
  public int getInt(String name) {
    return Integer.parseInt(m_Stored.get(name));
  }

  /**
   * Returns the float value associated with an option name.
   *
   * @param name	the name
   * @return		the associated value
   */
  public float getFloat(String name) {
    return Float.parseFloat(m_Stored.get(name));
  }

  /**
   * Returns the stored options in a string representation.
   *
   * @return		the string represetation
   */
  public String toString() {
    return m_Stored.toString();
  }
}
