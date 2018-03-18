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
 * ArffViewer.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

/**
 * Launches the Weka ArffViewer.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArffViewer
  extends AbstractGUILaunchCommand
  implements DatasetHandler {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "arffviewer";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Launches the Weka Arff viewer.\n"
      + "You can supply dataset filenames to load immediately in the viewer.";
  }

  /**
   * Returns whether the command utilizes additional arguments that get passed on.
   *
   * @return		true if additional options
   */
  @Override
  public boolean supportsAdditionalArguments() {
    return true;
  }

  /**
   * Returns the GUI classes to check.
   *
   * @return		the class names
   */
  protected String[] getGUIClasses() {
    return new String[]{"weka.gui.arffviewer.ArffViewer"};
  }
}
