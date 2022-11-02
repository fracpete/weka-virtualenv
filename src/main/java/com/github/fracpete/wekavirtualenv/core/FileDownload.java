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
 * FileDownload.java
 * Copyright (C) 2019-2022 University of Waikato, Hamilton, NZ
 * Copyright (C) Apache Software Foundation
 */

package com.github.fracpete.wekavirtualenv.core;

import com.github.fracpete.requests4j.response.AbstractResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

/**
 * For downloading files with progress being output in the console.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileDownload
  extends AbstractResponse {

  /** the file to write to. */
  protected File m_OutputFile;

  /** the buffer size (<= 0 for default). */
  protected int m_BufferSize;

  /** for outputting the feedback. */
  protected ConsoleOutputSupporter m_Progress;

  /**
   * Initializes the response.
   *
   * @param outputFilename 	the file to write the response to
   * @param progress 		the object to use for outputting the progress, ignored if null
   */
  public FileDownload(String outputFilename, ConsoleOutputSupporter progress) {
    this(new File(outputFilename), -1, progress);
  }

  /**
   * Initializes the response.
   *
   * @param outputFilename 	the file to write the response to
   * @param bufferSize 	the buffer size, use <= 0 for default
   * @param progress 		the object to use for outputting the progress, ignored if null
   */
  public FileDownload(String outputFilename, int bufferSize, ConsoleOutputSupporter progress) {
    this(new File(outputFilename), bufferSize, progress);
  }

  /**
   * Initializes the response.
   *
   * @param outputFile 	the file to write the response to
   * @param progress 		the object to use for outputting the progress, ignored if null
   */
  public FileDownload(File outputFile, ConsoleOutputSupporter progress) {
    this(outputFile, -1, progress);
  }

  /**
   * Initializes the response.
   *
   * @param outputFile 	the file to write the response to
   * @param bufferSize 	the buffer size, use <= 0 for default
   * @param progress 		the object to use for outputting the progress, ignored if null
   */
  public FileDownload(File outputFile, int bufferSize, ConsoleOutputSupporter progress) {
    super();
    if (bufferSize <= 0)
      bufferSize = -1;
    m_OutputFile = outputFile;
    m_BufferSize = bufferSize;
    m_Progress   = progress;
  }

  /**
   * Returns the object used for outputting the progress.
   *
   * @return		the progress object, null if none set
   */
  public ConsoleOutputSupporter getProgress() {
    return m_Progress;
  }

  /**
   * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
   * <code>OutputStream</code>.
   * <p>
   * This method buffers the input internally, so there is no need to use a
   * <code>BufferedInputStream</code>.
   * Based on: org.apache.tika.io.IOUtils.copyLarge
   *
   * @param input  the <code>InputStream</code> to read from
   * @param output  the <code>OutputStream</code> to write to
   * @return the number of bytes copied
   * @throws NullPointerException if the input or output is null
   * @throws IOException if an I/O error occurs
   * @since Commons IO 1.3
   */
  public long copy(InputStream input, OutputStream output) throws IOException {
    DecimalFormat 	dformat;
    byte[] 		buffer;
    long 		count;
    int 		n;
    int 		i;
    boolean 		progress;

    dformat = new DecimalFormat("###,###.###");
    buffer  = new byte[1024 * 4];
    count   = 0;
    i       = 0;
    progress = (m_Progress != null);
    while ((n = input.read(buffer)) != -1) {
      output.write(buffer, 0, n);
      count += n;
      i++;
      if (progress && (i % 100 == 0))
        m_Progress.println(dformat.format((double) count / 1024.0) + "KB", true);
    }
    if (progress)
      m_Progress.println(dformat.format((double) count / 1024.0) + "KB", true);
    return count;
  }
}
