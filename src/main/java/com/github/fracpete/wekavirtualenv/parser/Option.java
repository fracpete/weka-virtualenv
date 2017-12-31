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
 * Option.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.parser;

import java.io.Serializable;

/**
 * Defines an option.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Option
  implements Serializable, Comparable<Option> {

  /** the destination (key in namespace). */
  protected String m_Dest;

  /** the commandline flag. */
  protected String m_Flag;

  /** whether option has an argument. */
  protected boolean m_HasArgument;

  /** the default value. */
  protected String m_DefaultValue;

  /** the help string. */
  protected String m_Help;

  /** whether the option is required or optional. */
  protected boolean m_Required;

  /**
   * Initializes the option.
   *
   * @param flag	the flag
   */
  public Option(String flag) {
    this(flag.replace("-", ""), flag, true, "", flag.replace("-", ""), false);
  }

  /**
   * Initializes the option.
   *
   * @param dest	the destination (ie key in namespace)
   * @param flag	the flag (eg "--blah")
   * @param hasArg	true if the option has an argument
   * @param defValue	the default value
   * @param help	short help string
   * @param required	true if required, otherwise optional
   */
  public Option(String dest, String flag, boolean hasArg, String defValue, String help, boolean required) {
    m_Dest         = dest;
    m_Flag         = flag;
    m_HasArgument  = hasArg;
    m_DefaultValue = defValue;
    m_Help         = help;
    m_Required     = required;
  }

  /**
   * Sets the destination key (used in the namespace).
   *
   * @param value	the name of the key
   * @return		the option
   */
  public Option dest(String value) {
    m_Dest = value;
    return this;
  }

  /**
   * Sets whether the option requires an argument.
   *
   * @param value	true if argument required
   * @return		the option
   */
  public Option argument(boolean value) {
    m_HasArgument = value;
    return this;
  }

  /**
   * Sets the default value.
   *
   * @param value	the default value
   * @return		the option
   */
  public Option setDefault(String value) {
    m_DefaultValue = value;
    return this;
  }

  /**
   * Sets the help string.
   *
   * @param value	the help string
   * @return		the option
   */
  public Option help(String value) {
    m_Help = value;
    return this;
  }

  /**
   * Sets whether the option is required.
   *
   * @param value	true if required, optional otherwise
   * @return		the option
   */
  public Option required(boolean value) {
    m_Required = value;
    return this;
  }

  /**
   * Returns the destination key name.
   *
   * @return		the key
   */
  public String getDest() {
    return m_Dest;
  }

  /**
   * Returns the commandline flag.
   *
   * @return		the flag
   */
  public String getFlag() {
    return m_Flag;
  }

  /**
   * Returns whether the option requires an argument.
   *
   * @return		true if argument required
   */
  public boolean hasArgument() {
    return m_HasArgument;
  }

  /**
   * Returns the default value.
   *
   * @return		the default value
   */
  public String getDefault() {
    return m_DefaultValue;
  }

  /**
   * Returns the help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return m_Help;
  }

  /**
   * Returns whether the option is required or optional.
   *
   * @return		true if required, optional otherwise
   */
  public boolean isRequired() {
    return m_Required;
  }

  /**
   * Uses string comparison on the name.
   *
   * @param o		the other option to compare with
   * @return		less than, equal to or greater than zero
   */
  @Override
  public int compareTo(Option o) {
    return m_Dest.compareTo(o.getDest());
  }

  /**
   * Checks whether the option is the same (uses name).
   *
   * @param obj		the object/option to compare with
   * @return		true if option with the same name
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Option) && (compareTo((Option) obj) == 0);
  }

  /**
   * Returns the hashcode.
   *
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return m_Dest.hashCode();
  }

  /**
   * Returns a short description of the option.
   *
   * @return		the description
   */
  public String toString() {
    return "name=" + m_Dest + ", "
      + "flag=" + m_Flag + ", "
      + "hasArg=" + m_HasArgument + ", "
      + "defValue=" + m_DefaultValue + ", "
      + "help=" + m_Help + ", "
      + "required=" + m_Required;
  }
}
