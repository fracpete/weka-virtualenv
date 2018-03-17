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
 * Internet.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

import nz.ac.waikato.cms.core.FileUtils;
import nz.ac.waikato.cms.core.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Helper class for internet related tasks.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Internet {

  /**
   * Downloads a file.
   *
   * @param remote	the URL to download
   * @param local	the local destination
   * @param verbose	whether to output some progress information
   * @return		null if successful, otherwise error message
   */
  public static String download(String remote, String local, boolean verbose) {
    String			result;
    URL 			url;
    BufferedInputStream 	input;
    BufferedOutputStream 	output;
    FileOutputStream 		fos;
    byte[]			buffer;
    int				len;
    int				count;
    int 			size;
    HttpURLConnection 		conn;
    DecimalFormat 		dformat;

    ProxyUtils.applyProxy();

    input    = null;
    output   = null;
    fos      = null;
    dformat  = new DecimalFormat("###,###.###");
    if (verbose)
      System.out.println("Downloading: " + remote + " to " + local);
    try {
      url  = new URL(remote);
      conn = (HttpURLConnection) url.openConnection();
      input  = new BufferedInputStream(conn.getInputStream());
      fos    = new FileOutputStream(new File(local));
      output = new BufferedOutputStream(fos);
      buffer = new byte[1024];
      count  = 0;
      size   = 0;
      while ((len = input.read(buffer)) > 0) {
	count++;
	size += len;
	output.write(buffer, 0, len);
	if (count % 100 == 0) {
	  output.flush();
	  if (verbose)
	    System.out.println(dformat.format((double) size / 1024.0) + "KB");
	}
      }
      output.flush();
      if (verbose)
	System.out.println(dformat.format((double) size / 1024.0) + "KB");

      result = null;
    }
    catch (Exception e) {
      result = "Problem downloading '" + remote + "' to '" + local + "':\n"
	+ Utils.throwableToString(e);
    }
    finally {
      FileUtils.closeQuietly(input);
      FileUtils.closeQuietly(output);
      FileUtils.closeQuietly(fos);
    }

    return result;
  }
}
