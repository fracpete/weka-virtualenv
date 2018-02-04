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
 * Grep.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.filter;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;

import java.util.regex.Pattern;

/**
 * Captures matching strings similar to the unix 'grep' command.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Grep
  extends AbstractFilter {

  /** the pattern for matching. */
  protected Pattern m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * The name of the filter (used on the filterline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "grep";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "For capturing strings that match a regular expression.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  public ArgumentParser getParser() {
    ArgumentParser 	result;

    result = super.getParser();
    result.addOption("--regexp")
      .dest("regexp")
      .help("the regular expression that the output must match to be kept.")
      .required(true);
    result.addOption("--invert")
      .dest("invert")
      .help("whether to invert the matching sense.")
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
      try {
	m_RegExp = Pattern.compile(ns.getString("regexp"));
      }
      catch (Exception e) {
        addError("Invalid regular expression: " + ns.getString("regexp"), e);
        return false;
      }

      m_Invert = ns.getBoolean("invert");
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
    if ((!m_Invert && m_RegExp.matcher(line).matches())
      || (m_Invert && !m_RegExp.matcher(line).matches())) {
      return line;
    }
    return null;
  }
}
