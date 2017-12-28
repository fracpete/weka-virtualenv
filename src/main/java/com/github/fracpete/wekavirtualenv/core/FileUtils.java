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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
   * Closes the input stream.
   *
   * @param istream	the stream to close
   */
  public static void closeQuietly(InputStream istream) {
    try {
      if (istream != null)
	istream.close();
    }
    catch (IOException e) {
      // ignore
    }
  }

  /**
   * Closes the output stream.
   *
   * @param ostream	the stream to close
   */
  public static void closeQuietly(OutputStream ostream) {
    try {
      if (ostream != null) {
        ostream.flush();
	ostream.close();
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

  /**
   * Copies or moves files and directories (recursively).
   * If targetLocation does not exist, it will be created.
   * <br><br>
   * Original code from <a href="http://www.java-tips.org/java-se-tips/java.io/how-to-copy-a-directory-from-one-location-to-another-loc.html" target="_blank">Java-Tips.org</a>.
   *
   * @param sourceLocation	the source file/dir
   * @param targetLocation	the target file/dir
   * @param move		if true then the source files/dirs get deleted
   * 				as soon as copying finished
   * @param atomic		whether to perform an atomic move operation
   * @return			false if failed to delete when moving or failed to create target directory
   * @throws IOException	if copying/moving fails
   */
  public static boolean copyOrMove(File sourceLocation, File targetLocation, boolean move, boolean atomic) throws IOException {
    String[] 		children;
    int 		i;
    Path source;
    Path 		target;

    if (sourceLocation.isDirectory()) {
      if (!targetLocation.exists()) {
	if (!targetLocation.mkdir())
	  return false;
      }

      children = sourceLocation.list();
      for (i = 0; i < children.length; i++) {
        if (!copyOrMove(
            new File(sourceLocation.getAbsoluteFile(), children[i]),
            new File(targetLocation.getAbsoluteFile(), children[i]),
            move, atomic))
          return false;
      }

      if (move)
        return sourceLocation.delete();
      else
	return true;
    }
    else {
      source = FileSystems.getDefault().getPath(sourceLocation.getAbsolutePath());
      if (targetLocation.isDirectory())
        target = FileSystems.getDefault().getPath(targetLocation.getAbsolutePath() + File.separator + sourceLocation.getName());
      else
        target = FileSystems.getDefault().getPath(targetLocation.getAbsolutePath());
      if (move) {
	if (atomic)
	  Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
	else
	  Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
      }
      else {
	Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      }
      return true;
    }
  }
}
