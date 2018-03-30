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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

/**
 * Helper class for internet related tasks.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Internet {

  /**
   * Opens the URL.
   *
   * @param url		the URL to open
   * @return		the connection
   * @throws IOException	if opening fails
   */
  protected static URLConnection openConnection(URL url) throws IOException {
    return url.openConnection();
  }

  /**
   * Gets the connection, handles redirects.
   * Taken from weka.core.packageManagement.DefaultPackageManager.
   *
   * @param url		the URL to open
   * @return		the connection
   * @throws IOException	if opening fails
   */
  protected static URLConnection getConnection(URL url) throws IOException {
    URLConnection conn = openConnection(url);

    if (conn instanceof HttpURLConnection) {
      int status = 0;
      try {
        status = ((HttpURLConnection) conn).getResponseCode();
      }
      catch (Exception ex) {
        if (url.toString().startsWith("https://")) {
          String newURL = url.toString().replace("https://", "http://");
          conn = openConnection(new URL(newURL));
          status = ((HttpURLConnection) conn).getResponseCode();
        }
        else {
          throw ex;
        }
      }
      int redirectCount = 0;
      while (status == HttpURLConnection.HTTP_MOVED_TEMP
        || status == HttpURLConnection.HTTP_MOVED_PERM
        || status == HttpURLConnection.HTTP_SEE_OTHER) {
        redirectCount++;
        if (redirectCount > 2) {
          throw new IOException(
            "Three redirects were generated when trying to " + "download "
              + url);
        }

        String newURL = conn.getHeaderField("Location");
        try {
          conn = openConnection(new URL(newURL));
          status = ((HttpURLConnection) conn).getResponseCode();
        }
        catch (Exception ex) {
          if (newURL.startsWith("https://")) {
            // try http instead
            System.out.println("Trying http instead of https for " + newURL);
            newURL = newURL.replace("https://", "http://");
            conn = openConnection(new URL(newURL));
            status = ((HttpURLConnection) conn).getResponseCode();
          }
          else {
            throw ex;
          }
        }
      }
    }

    return conn;
  }

  /**
   * Downloads a file.
   *
   * @param remote	the URL to download
   * @param local	the local destination
   * @param verbose	whether to output some progress information
   * @param capture 	for capturing the output
   * @return		null if successful, otherwise error message
   */
  public static String download(String remote, String local, boolean verbose, OutputCapture capture) {
    String			result;
    URL 			url;
    BufferedInputStream 	input;
    BufferedOutputStream 	output;
    FileOutputStream 		fos;
    byte[]			buffer;
    int				len;
    int				count;
    int 			size;
    URLConnection 		conn;
    DecimalFormat 		dformat;

    ProxyUtils.applyProxy();

    input    = null;
    output   = null;
    fos      = null;
    dformat  = new DecimalFormat("###,###.###");
    if (verbose)
      capture.println("Downloading: " + remote + " to " + local, true);
    try {
      url    = new URL(remote);
      conn   = getConnection(url);
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
	    capture.println(dformat.format((double) size / 1024.0) + "KB", true);
	}
      }
      output.flush();
      if (verbose)
	capture.println(dformat.format((double) size / 1024.0) + "KB", true);

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
