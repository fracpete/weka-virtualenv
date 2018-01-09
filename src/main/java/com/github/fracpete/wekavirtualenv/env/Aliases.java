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
 * Aliases.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.env;

import com.github.fracpete.wekavirtualenv.core.Project;
import nz.ac.waikato.cms.core.FileUtils;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Helper class for aliases.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Aliases {

  /** The properties file containing the information about the aliases. */
  public final static String SETUP = "aliases.props";

  /**
   * Returns the path for the setup props file.
   *
   * @param env		the environment name, if env-specific, null for global ones
   */
  public static String setupPath(String env) {
    if (env == null)
      return Project.getHomeDir() + File.separator + SETUP;
    else
      return Environments.getEnvDir(env) + File.separator + SETUP;
  }

  /**
   * Reads the alias definitions.
   *
   * @param file 	the definition props file
   * @return		the alias properties, null if failed to read or invalid
   */
  public static Properties read(File file) {
    FileReader 	freader;
    Properties 	result;

    if (!file.exists())
      return null;

    freader = null;
    try {
      freader = new FileReader(file);
      result = new Properties();
      result.load(freader);
    }
    catch (Exception e) {
      result = null;
      System.err.println("Failed to read alias definitions: " + file);
      e.printStackTrace();
    }
    finally {
      FileUtils.closeQuietly(freader);
    }

    return result;
  }

  /**
   * Saves the alias definitions to the specified file.
   *
   * @param alias 	the aliases to save
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  public static String write(Properties alias, File file) {
    String		result;
    FileWriter 		fwriter;
    BufferedWriter 	bwriter;

    result = null;

    fwriter = null;
    bwriter = null;
    try {
      fwriter = new FileWriter(file);
      bwriter = new BufferedWriter(fwriter);
      alias.store(bwriter, null);
    }
    catch (Exception e) {
      result = "Failed to save alias definitions to: " + file + "\n" + e;
    }
    finally {
      FileUtils.closeQuietly(bwriter);
      FileUtils.closeQuietly(fwriter);
    }

    return result;
  }

  /**
   * Adds (or replaces) the command alias.
   *
   * @param env		the environment, if to store per environment; null for global alias
   * @param alias       the name for the alias
   * @param options	the options to associate with the alias
   * @return		null if successful, otherwise error message
   */
  public static String add(String env, String alias, String[] options) {
    String	result;
    Properties	props;
    String	cmdline;
    File	setupFile;

    cmdline = OptionUtils.joinOptions(options);
    if (cmdline.trim().isEmpty())
      return "No parameters provided (" + info(env, alias) + ")!";

    setupFile = new File(setupPath(env));
    if (setupFile.exists()) {
      props = read(setupFile);
      if (props == null)
        return "Failed to read alias definitions (" + info(env, alias) + "): " + setupFile;
    }
    else {
      props = new Properties();
    }
    props.setProperty(alias, cmdline);
    result = write(props, setupFile);

    return result;
  }

  /**
   * Removes the command alias.
   *
   * @param env		the environment, if to remove from specific environment; null for global alias
   * @param alias       the name for the alias
   * @return		null if successful, otherwise error message
   */
  public static String remove(String env, String alias) {
    String	result;
    Properties	props;
    File	setupFile;

    setupFile = new File(setupPath(env));
    if (setupFile.exists()) {
      props = read(setupFile);
      if (props == null)
        return "Failed to read alias definitions (" + info(env, alias) + "): " + setupFile;
    }
    else {
      return "No alias definitions available, cannot remove alias (" + info(env) + "): " + alias;
    }
    if (!props.containsKey(alias))
      return "Unknown alias (" + info(env) + "): " + alias;
    props.remove(alias);
    result = write(props, setupFile);

    return result;
  }

  /**
   * Generates an information snippet using the environment.
   *
   * @param env		the environment, can be null
   * @return		the snippet
   */
  protected static String info(String env) {
    if (env == null)
      return "env=<global>";
    else
      return "env=" + env;
  }

  /**
   * Generates an information snippet using the environment and alias.
   *
   * @param env		the environment, can be null
   * @param alias	the alias
   * @return		the snippet
   */
  protected static String info(String env, String alias) {
    return info(env) + ", alias=" + alias;
  }

  /**
   * Returns the command alias.
   *
   * @param env		the environment, if to remove from specific environment; null for global alias
   * @param alias       the name for the alias
   * @param errors	for collecting errors
   * @return		the alias options, null if failed to retrieve
   */
  public static String[] get(String env, String alias, List<String> errors) {
    String[]	result;
    Properties	props;
    String	cmdline;
    File	setupFile;

    setupFile = new File(setupPath(env));
    if (setupFile.exists()) {
      props = read(setupFile);
      if (props == null) {
	errors.add("Failed to read alias definitions (" + info(env, alias) + "): " + setupFile);
	return null;
      }
    }
    else {
      errors.add("No alias definitions available (" + info(env, alias) + "): " + setupFile);
      return null;
    }
    cmdline = props.getProperty(alias);
    if (cmdline == null) {
      errors.add("Alias not present in definitions (" + info(env, alias) + "): " + setupFile);
      return null;
    }
    else {
      try {
	result = OptionUtils.splitOptions(cmdline);
      }
      catch (Exception e) {
        errors.add("Failed to split command-line (" + info(env, alias) + "): " + cmdline);
        return null;
      }
    }

    return result;
  }

  /**
   * Lists the aliases. Format: name<TAB>cmdline
   *
   * @param env		the environment, if to list for specific environment; null for global aliases
   * @param errors	for storing errors
   * @return		the list, null if failed to list
   */
  public static List<String> list(String env, List<String> errors) {
    List<String>	result;
    Properties		props;
    File		setupFile;

    result    = new ArrayList<>();
    setupFile = new File(setupPath(env));
    if (setupFile.exists()) {
      props = read(setupFile);
      if (props == null) {
	errors.add("Failed to read alias definitions (" + info(env) + "): " + setupFile);
	return null;
      }
      else {
        for (String key: props.stringPropertyNames())
          result.add(key + "\t" + props.getProperty(key));
        Collections.sort(result);
      }
    }

    return result;
  }

  /**
   * Lists all aliases. Format: env<TAB>name<TAB>cmdline
   *
   * @param errors	for storing errors
   * @return		the list, null if failed to list
   */
  public static List<String> listAll(List<String> errors) {
    List<String>	result;
    List<String>	sub;

    result = new ArrayList<>();

    // global
    sub = list(null, errors);
    if (sub != null) {
      for (String alias: sub) {
	result.add("<global>\t" + alias);
      }
    }
    if (!errors.isEmpty())
      return null;

    // iterate environments
    for (Environment env: Environments.list()) {
      sub = list(env.name, errors);
      if (sub != null) {
	for (String alias: sub) {
	  result.add(env.name + "\t" + alias);
	}
      }
      if (!errors.isEmpty())
	return null;
    }

    return result;
  }
}
