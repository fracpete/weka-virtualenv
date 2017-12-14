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
 * Envs.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Helper class for environments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Environments {

  /** The properties file containing the information about an environment. */
  public final static String INFO_PROPS = "env.props";

  /** the name of the environment. */
  public final static String KEY_NAME = "name";

  /** the java binary of the environment. */
  public final static String KEY_JAVA = "java";

  /** the heap of the environment. */
  public final static String KEY_HEAP = "heap";

  /**
   * Turns the environment name into a directory name.
   *
   * @param name	the name to convert
   * @return		the directory name
   */
  public static String nameToDir(String name) {
    StringBuilder	result;
    int			i;
    char		c;

    result = new StringBuilder();
    for (i = 0; i < name.length(); i++) {
      c = name.charAt(i);
      if ((c >= 'a') && (c <= 'z'))
        result.append(c);
      else if ((c >= 'A') && (c <= 'Z'))
        result.append(c);
      else if ((c >= '0') && (c <= '9'))
        result.append(c);
      else if (c == '.')
        result.append(c);
      else if (c == '-')
        result.append(c);
      else if (c == '_')
        result.append("_");
      else
        result.append("_");
    }

    return result.toString();
  }

  /**
   * Reads the environment.
   *
   * @param dir		the directory of the environment
   * @return		the information, null if nothing available
   */
  protected static Environment readEnv(File dir) {
    Environment result;
    File	file;
    FileReader	freader;
    Properties	props;

    result = null;

    file = new File(dir.getAbsolutePath() + File.separator + INFO_PROPS);
    if (!file.exists())
      return null;

    freader = null;
    try {
      freader = new FileReader(file);
      props   = new Properties();
      props.load(freader);
      if (props.getProperty(KEY_NAME) != null) {
        result = new Environment();
        result.name = props.getProperty(KEY_NAME);
        result.java = props.getProperty(KEY_JAVA);
        result.heap = props.getProperty(KEY_HEAP);
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
   * Reads the environment with the given name.
   *
   * @param name	the name of the environment
   * @return		the environment, null if failed to read
   */
  public static Environment readEnv(String name) {
    File	env;

    env = new File(Project.getEnvsDir() + File.separator + nameToDir(name));
    return readEnv(env);
  }

  /**
   * Lists all environments.
   *
   * @return		the available environments
   */
  public static List<Environment> list() {
    List<Environment>	result;
    File		envs;
    Environment		env;

    result = new ArrayList<>();
    envs   = new File(Project.getEnvsDir());
    if (envs.exists()) {
      for (File file : envs.listFiles()) {
	env = readEnv(file);
	if (env != null)
	  result.add(env);
      }
    }

    return result;
  }
}
