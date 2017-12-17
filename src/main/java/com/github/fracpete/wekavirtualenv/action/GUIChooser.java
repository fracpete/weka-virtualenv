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
 * GUIChooser.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.action;

import com.github.fracpete.wekavirtualenv.parser.Namespace;

/**
 * Launches the Weka GUIChooser.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GUIChooser
  extends AbstractLaunchCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "guichooser";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Launches the Weka GUIChooser.";
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  protected boolean doExecute(Namespace ns, String[] options) {
    return launch(build("weka.gui.GUIChooser", options));
  }
}
