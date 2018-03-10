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
 * ZipUtils.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache compress commons
 */
package com.github.fracpete.wekavirtualenv.core;

import nz.ac.waikato.cms.core.FileUtils;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * A helper class for ZIP-file related tasks.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ZipUtils {

  /**
   * Unzips the files in a ZIP file.
   *
   * Taken from here:
   * http://commons.apache.org/compress/examples.html
   *
   * @param input	the ZIP file to unzip
   * @param outputDir	the directory where to store the extracted files
   * @param createDirs	whether to re-create the directory structure from the
   * 			ZIP file
   * @param errors	for storing potential errors
   * @return		the successfully extracted files
   */
  public static List<File> decompress(File input, File outputDir, boolean createDirs, StringBuilder errors) {
    List<File>				result;
    ZipFile				archive;
    Enumeration<ZipArchiveEntry>	enm;
    ZipArchiveEntry			entry;
    File				outFile;
    String				outName;
    byte[]				buffer;
    BufferedInputStream			in;
    BufferedOutputStream		out;
    FileOutputStream			fos;
    int					len;
    String				error;
    long				read;

    result  = new ArrayList<>();
    archive = null;
    try {
      // unzip archive
      buffer  = new byte[1024];
      archive = new ZipFile(input.getAbsoluteFile());
      enm     = archive.getEntries();
      while (enm.hasMoreElements()) {
	entry = enm.nextElement();

	if (entry.isDirectory() && !createDirs)
	  continue;

	// extract
	if (entry.isDirectory() && createDirs) {
	  outFile = new File(outputDir.getAbsolutePath() + File.separator + entry.getName());
	  if (!outFile.mkdirs()) {
	    error = "Failed to create directory '" + outFile.getAbsolutePath() + "'!";
	    System.err.println(error);
	    errors.append(error + "\n");
	  }
	}
	else {
	  in      = null;
	  out     = null;
	  fos     = null;
	  outName = null;
	  try {
	    // assemble output name
	    outName = outputDir.getAbsolutePath() + File.separator;
	    if (createDirs)
	      outName += entry.getName();
	    else
	      outName += new File(entry.getName()).getName();

	    // create directory, if necessary
	    outFile = new File(outName).getParentFile();
	    if (!outFile.exists()) {
	      if (!outFile.mkdirs()) {
		error =
		    "Failed to create directory '" + outFile.getAbsolutePath() + "', "
		    + "skipping extraction of '" + outName + "'!";
		System.err.println(error);
		errors.append(error + "\n");
		continue;
	      }
	    }

	    // extract data
	    in   = new BufferedInputStream(archive.getInputStream(entry));
	    fos  = new FileOutputStream(outName);
	    out  = new BufferedOutputStream(fos, 1024);
	    read = 0;
	    while (read < entry.getSize()) {
	      len   = in.read(buffer);
	      read += len;
	      out.write(buffer, 0, len);
	    }
	    result.add(new File(outName));
	  }
	  catch (Exception e) {
	    error = "Error extracting '" + entry.getName() + "' to '" + outName + "': " + e;
	    System.err.println(error);
	    errors.append(error + "\n");
	  }
	  finally {
	    FileUtils.closeQuietly(in);
	    FileUtils.closeQuietly(out);
	    FileUtils.closeQuietly(fos);
	  }
	}
      }
    }
    catch (Exception e) {
      e.printStackTrace();
      errors.append("Error occurred: " + e + "\n");
    }
    finally {
      if (archive != null) {
	try {
	  archive.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
    }

    return result;
  }
}
