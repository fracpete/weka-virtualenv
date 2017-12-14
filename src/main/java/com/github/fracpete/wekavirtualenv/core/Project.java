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
 * Project.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;

/**
 * Helper class for the project.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Project {

  /**
   * Returns the home directory for the project.
   *
   * @return 		the project's home directory
   */
  public static String getHomeDir() {
    String	result;

    if (SystemUtils.IS_OS_WINDOWS)
      result = System.getProperty("user.home");
    else if (SystemUtils.IS_OS_UNIX)
      result = System.getProperty("user.home") + File.separator + ".local" + File.separator + "share";
    else
      throw new IllegalStateException("Unhandled OS: " + System.getProperty("os.name"));

    result = result + File.separator + "wekavirtualenv";

    return result;
  }

  /**
   * Returns the directory for the environments.
   *
   * @return 		the environment directory
   */
  public static String getEnvsDir() {
    return getHomeDir() + File.separator + "envs";
  }

}
