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
 * Settings.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.core;

import com.github.fracpete.wekavirtualenv.core.Project;
import nz.ac.waikato.cms.core.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * For storing/restoring settings.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Settings {

  /**
   * Returns the file for storing the settings.
   *
   * @return		the filename
   */
  public static String getSettingsFile() {
    return Project.getHomeDir() + File.separator + "uisettings.props";
  }

  /**
   * Loads the settings. Will be empty if no settings file exists
   *
   * @return		the current settings
   * @see		#getSettingsFile()
   */
  public static Properties load() {
    Properties		result;
    File		file;
    FileReader		freader;
    BufferedReader	breader;

    result = new Properties();
    file   = new File(getSettingsFile());
    if (file.exists()) {
      freader = null;
      breader = null;
      try {
        freader = new FileReader(file);
        breader = new BufferedReader(freader);
	result.load(breader);
      }
      catch (Exception e) {
        // ignored
      }
      finally {
	FileUtils.closeQuietly(breader);
	FileUtils.closeQuietly(freader);
      }
    }

    return result;
  }

  /**
   * Saves the settings.
   *
   * @param settings	the settings to save
   * @return		true if successfully saved
   */
  public static boolean save(Properties settings) {
    boolean		result;
    FileWriter		fwriter;
    BufferedWriter	bwriter;

    result = true;

    fwriter = null;
    bwriter = null;
    try {
      fwriter = new FileWriter(getSettingsFile());
      bwriter = new BufferedWriter(fwriter);
      settings.store(bwriter, null);
    }
    catch (Exception e) {
      result = false;
    }
    finally {
      FileUtils.closeQuietly(bwriter);
      FileUtils.closeQuietly(fwriter);
    }

    return result;
  }
}
