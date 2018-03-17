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
 * Versions.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class around Weka versions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Versions {

  /** the versions file name. */
  public final static String VERSIONS_NAME = "versions.csv";

  /** the URL for the versions file. */
  public final static String VERSIONS_URL = "https://raw.githubusercontent.com/fracpete/weka-virtualenv/master/" + VERSIONS_NAME;

  /**
   * Returns the location of the versions file.
   *
   * @return		the file
   */
  public static String getVersionsFile() {
    return Project.getHomeDir() + File.separator + VERSIONS_NAME;
  }

  /**
   * Checks whether the versions file exists.
   *
   * @return		true if it exists
   */
  public static boolean isVersionsFilePresent() {
    return new File(getVersionsFile()).exists();
  }

  /**
   * Updates the versions file.
   *
   * @param verbose	whether to output some progress information
   * @param capture 	for capturing output
   * @return		null if successful, otherwise error message
   */
  public static String update(boolean verbose, OutputCapture capture) {
    return Internet.download(VERSIONS_URL, getVersionsFile(), verbose, capture);
  }

  /**
   * Returns a list of available Weka versions.
   *
   * @return		the versions
   * @throws Exception	if reading or parsing fails
   */
  public static List<String> getAvailableVersions() throws Exception {
    List<String>	result;
    List<String>	lines;
    String[]		parts;

    result = new ArrayList<>();
    lines = Files.readAllLines(new File(getVersionsFile()).toPath());
    for (String line: lines) {
      parts = line.split(",");
      if (parts.length == 2)
	result.add(parts[0]);
    }

    return result;
  }

  /**
   * Returns the URL for the specified Weka versions.
   *
   * @param version	the version to get the link for
   * @return		the URL, null if not found
   * @throws Exception	if reading or parsing fails
   */
  public static String getURL(String version) throws Exception {
    String		result;
    List<String>	lines;
    String[]		parts;

    result = null;
    lines = Files.readAllLines(new File(getVersionsFile()).toPath());
    for (String line: lines) {
      parts = line.split(",");
      if (parts.length == 2) {
        if (parts[0].equals(version)) {
          result = parts[1];
          break;
	}
      }
    }

    return result;
  }
}
