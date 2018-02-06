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
 * DumpVars.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.OutputListener;
import nz.ac.waikato.cms.core.Utils;

/**
 * Just outputs all the currently set variables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DumpVars
  extends AbstractScriptCommandWithOutputListeners {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "dump_vars";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Just outputs all the currently set variables.";
  }

  /**
   * For outputting the line.
   *
   * @param line	the line to output
   */
  protected void output(String line) {
    System.out.println(line);
    for (OutputListener l: m_OutputListeners)
      l.outputOccurred(line, true);
  }

  /**
   * Evaluates the script command.
   *
   * @param ns		the namespace
   * @param options	the options
   * @return		true if successful
   */
  @Override
  protected boolean evalCommand(Namespace ns, String[] options) {
    Object	val;

    for (String name: getContext().getVariables().names()) {
      val = getContext().getVariables().get(name);
      if (val instanceof String)
	output(name + "=" + val);
      else
        output(name + "=" + Utils.flatten((String[]) val, ", "));
    }
    return true;
  }
}
