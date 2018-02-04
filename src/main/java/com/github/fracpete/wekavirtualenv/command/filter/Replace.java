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
 * Tee.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.filter;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;

/**
 * Performs string replacement, simple or regular expression based.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Replace
  extends AbstractFilter {

  /** the string to find. */
  protected String m_Find;

  /** the replacement string. */
  protected String m_Replace;

  /** whether to use regexp matching. */
  protected boolean m_RegExp;

  /** whether to replace all occurrences (regexp only). */
  protected boolean m_All;

  /**
   * The name of the filter (used on the filterline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "replace";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Performs string replacement, simple or regular expression based.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  public ArgumentParser getParser() {
    ArgumentParser 	result;

    result = super.getParser();
    result.addOption("--find")
      .dest("find")
      .help("the string or pattern to find.")
      .required(true);
    result.addOption("--replace")
      .dest("replace")
      .help("the replacement string to use.")
      .required(true);
    result.addOption("--regexp")
      .dest("regexp")
      .help("whether to use regular expression matching.")
      .argument(false);
    result.addOption("--all")
      .dest("all")
      .help("whether to replace all occurrences in case of regexp matching.")
      .argument(false);

    return result;
  }

  /**
   * Initializes the filter with the parsed options.
   *
   * @param ns		the parsed options
   * @return		true if successfully parsed
   */
  public boolean initialize(Namespace ns) {
    boolean	result;

    result = super.initialize(ns);

    if (result) {
      m_Find    = ns.getString("find");
      m_Replace = ns.getString("replace");
      m_RegExp  = ns.getBoolean("regexp");
      if (m_RegExp)
	m_All = ns.getBoolean("all");
    }

    return result;
  }

  /**
   * Intercepts the process output.
   *
   * @param line	the output to process
   * @param stdout	whether stdout or stderr
   * @return		the string to keep or null
   */
  @Override
  protected String doIntercept(String line, boolean stdout) {
    String	result;

    if (m_RegExp) {
      if (m_All)
        result = line.replaceAll(m_Find, m_Replace);
      else
        result = line.replaceFirst(m_Find, m_Replace);
    }
    else {
      result = line.replace(m_Find, m_Replace);
    }

    return result;
  }
}
