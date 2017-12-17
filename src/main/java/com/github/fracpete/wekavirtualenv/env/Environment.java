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
 * Environment.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.env;

import com.github.fracpete.wekavirtualenv.core.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * Setup information of an environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Environment {

  /** the name of the environment. */
  public final static String KEY_NAME = "name";

  /** the java binary (full path). */
  public final static String KEY_JAVA = "java";

  /** the heap size. */
  public final static String KEY_MEMORY = "memory";

  /** the weka jar to use. */
  public final static String KEY_WEKA = "weka";

  /** the name of the environment. */
  public String name;

  /** the java binary ("" if default). */
  public String java;

  /** the heap size ("" if default). */
  public String memory;

  /** the weka jar (full path). */
  public String weka;

  /**
   * Returns the environment in simple string representation.
   *
   * @return		the generated string
   */
  public String toString() {
    return toString("");
  }

  /**
   * Returns the environment in simple string representation.
   *
   * @param prefix	the prefix to use, eg indentation
   * @return		the generated string
   */
  public String toString(String prefix) {
    StringBuilder	result;

    result = new StringBuilder();
    result.append(prefix).append("Name: ").append(name).append("\n");
    result.append(prefix).append("Java: ").append(java.isEmpty() ? "<default>" : java).append("\n");
    result.append(prefix).append("Memory: ").append(memory.isEmpty() ? "<default>" : memory).append("\n");
    result.append(prefix).append("Weka: ").append(weka).append("\n");

    return result.toString();
  }

  /**
   * Reads the environment definition.
   *
   * @param file 	the definition props file
   * @return		the environment, null if failed to read or invalid
   */
  public static Environment read(File file) {
    Environment result;
    FileReader freader;
    Properties props;

    result = null;

    if (!file.exists())
      return null;

    freader = null;
    try {
      freader = new FileReader(file);
      props   = new Properties();
      props.load(freader);
      if ((props.getProperty(KEY_NAME) != null) && (props.getProperty(KEY_WEKA) != null)) {
        result = new Environment();
        result.name = props.getProperty(KEY_NAME);
        result.java = props.getProperty(KEY_JAVA);
        result.memory = props.getProperty(KEY_MEMORY);
        result.weka = props.getProperty(KEY_WEKA);
      }
    }
    catch (Exception e) {
      System.err.println("Failed to read: " + file);
      e.printStackTrace();
    }
    finally {
      FileUtils.closeQuietly(freader);
    }

    return result;
  }

  /**
   * Saves the environment to the specified file.
   *
   * @param env 	the environment to save
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  public static String save(Environment env, File file) {
    String		result;
    Properties		props;
    FileWriter		fwriter;
    BufferedWriter	bwriter;

    result = null;

    props = new Properties();
    props.setProperty(KEY_NAME, env.name);
    props.setProperty(KEY_JAVA, (env.java == null ? "" : env.java));
    props.setProperty(KEY_MEMORY, (env.memory == null ? "" : env.memory));
    props.setProperty(KEY_WEKA, env.weka);

    fwriter = null;
    bwriter = null;
    try {
      fwriter = new FileWriter(file);
      bwriter = new BufferedWriter(fwriter);
      props.store(bwriter, null);
    }
    catch (Exception e) {
      result = "Failed to save environment to: " + file + "\n" + e;
    }
    finally {
      FileUtils.closeQuietly(bwriter);
      FileUtils.closeQuietly(fwriter);
    }

    return result;
  }
}
