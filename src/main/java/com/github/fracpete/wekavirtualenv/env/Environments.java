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
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.env;

import com.github.fracpete.wekavirtualenv.core.Project;
import nz.ac.waikato.cms.core.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for environments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Environments {

  /** The properties file containing the information about an environment. */
  public final static String SETUP = "env.props";

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
    File	file;

    file = new File(dir.getAbsolutePath() + File.separator + SETUP);
    if (!file.exists())
      return null;

    return Environment.read(file);
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
   * Creates the environment.
   *
   * @param env		the environment setup
   * @return		null if successful, otherwise error message
   */
  public static String create(Environment env) {
    File	dir;

    dir = new File(Project.getEnvsDir() + File.separator + nameToDir(env.name));
    if (dir.exists())
      return "Environment already exists!\n" + "environment dir: " + dir;

    if (!dir.mkdirs())
      return "Failed to set up environment dir: " + dir;

    return Environment.save(env, new File(dir.getAbsolutePath() + File.separator + SETUP));
  }

  /**
   * Updates the environment.
   *
   * @param env		the environment setup
   * @return		null if successful, otherwise error message
   */
  public static String update(Environment env) {
    File	dir;

    dir = new File(Project.getEnvsDir() + File.separator + nameToDir(env.name));
    if (!dir.exists())
      return "Environment does not exist!\n" + "environment dir: " + dir;

    return Environment.save(env, new File(dir.getAbsolutePath() + File.separator + SETUP));
  }

  /**
   * Deletes the environment.
   *
   * @param name	the environment name
   * @return		null if successful, otherwise error message
   */
  public static String delete(String name) {
    File	dir;

    dir = new File(Project.getEnvsDir() + File.separator + nameToDir(name));
    if (!dir.exists())
      return "Environment does not exist!\n" + "environment dir: " + dir;

    if (FileUtils.delete(dir))
      return null;
    else
      return "Failed to delete environment directory: " + dir;
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

  /**
   * Returns the directory for the given environment.
   *
   * @param env 	the name of the environment
   * @return 		the environment directory
   */
  public static String getEnvDir(String env) {
    return Project.getHomeDir() + File.separator + "envs" + File.separator + nameToDir(env);
  }

  /**
   * Returns the "wekafiles" dir in the environment.
   *
   * @param env		the name of the environment
   * @return		the "wekafiles" dir of the environment
   */
  public static String getWekaFilesDir(String env) {
    return getEnvDir(env) + File.separator + "wekafiles";
  }
}
