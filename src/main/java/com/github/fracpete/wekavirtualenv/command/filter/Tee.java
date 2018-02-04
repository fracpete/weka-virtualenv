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
import nz.ac.waikato.cms.core.FileUtils;

import java.io.File;

/**
 * Tees off the output to a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Tee
  extends AbstractFilter {

  /** whether any output has been output yet. */
  protected boolean m_OutputOccurred;

  /** the output file. */
  protected File m_Output;

  /** whether to append. */
  protected boolean m_Append;

  /**
   * The name of the filter (used on the filterline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "tee";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Tees off the output to a file.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  public ArgumentParser getParser() {
    ArgumentParser 	result;

    result = super.getParser();
    result.addOption("--output")
      .dest("output")
      .help("the file to store the output in.")
      .required(true);
    result.addOption("--append")
      .dest("append")
      .help("whether to append to an existing output file.")
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
      m_Output = new File(ns.getString("output"));
      if (m_Output.isDirectory()) {
        addError("Output points to a directory: " + m_Output);
        return false;
      }

      m_Append = ns.getBoolean("append");
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
    if (!m_OutputOccurred) {
      if (!m_Append &&  m_Output.exists())
        m_Output.delete();
    }
    FileUtils.writeToFileMsg(m_Output.getAbsolutePath(), line, true, null);
    m_OutputOccurred = true;
    return line;
  }
}
