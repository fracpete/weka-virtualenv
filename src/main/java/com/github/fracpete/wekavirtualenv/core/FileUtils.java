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
 * FileUtils.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * File related utilities.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileUtils {

  /**
   * Closes the reader.
   *
   * @param reader	the reader to close
   */
  public static void closeQuietly(Reader reader) {
    try {
      if (reader != null)
	reader.close();
    }
    catch (IOException e) {
      // ignore
    }
  }

  /**
   * Closes the writer.
   *
   * @param writer	the writer to close
   */
  public static void closeQuietly(Writer writer) {
    try {
      if (writer != null) {
        writer.flush();
	writer.close();
      }
    }
    catch (IOException e) {
      // ignore
    }
  }

  /**
   * Deletes the specified file. If the file represents a directory, then this
   * will get deleted recursively.
   *
   * @param file	the file/dir to delete
   * @return		true if successfully deleted
   */
  public static boolean delete(File file) {
    boolean	result;
    File[]	files;

    result = true;

    if (file.isDirectory()) {
      files = file.listFiles();
      if (files != null) {
	for (File f : files) {
	  if (f.getName().equals(".") || f.getName().equals(".."))
	    continue;
	  result = delete(f);
	  if (!result)
	    return false;
	}
      }
    }

    result = file.delete();

    return result;
  }
}
