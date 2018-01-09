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
 * AbstractCommand.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.ArgumentParserException;
import com.github.fracpete.simpleargparse4j.InvalidEnvironmentException;
import com.github.fracpete.simpleargparse4j.Namespace;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for virtual environment commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractCommand
  implements Comparable<AbstractCommand> {

  /** the environment to use. */
  protected Environment m_Env;

  /** for storing any errors. */
  protected StringBuilder m_Errors;

  /**
   * Initializes the command.
   */
  public AbstractCommand() {
    super();
    m_Errors = null;
  }

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public abstract String getHelp();

  /**
   * Returns whether it requires an environment.
   *
   * @return		true if required
   */
  public boolean requiresEnvironment() {
    return false;
  }

  /**
   * Stores and outputs the error message.
   *
   * @param msg		the message
   */
  protected void addError(String msg) {
    if (m_Errors == null)
      m_Errors = new StringBuilder();
    else
      m_Errors.append("\n");
    m_Errors.append(msg);
  }

  /**
   * Returns whether any errors were recorded.
   *
   * @return		true if errors present
   */
  public boolean hasErrors() {
    return (m_Errors != null);
  }

  /**
   * Returns the errors.
   *
   * @return		the errors, null if none present
   */
  public String getErrors() {
    if (m_Errors == null)
      return null;
    else
      return m_Errors.toString();
  }

  /**
   * Hook method for loading the environment.
   * <br>
   * Instantiates the environment from the first parameter and removes this
   * parameter.
   *
   * @param options	the options to parse
   */
  public void loadEnv(String[] options) {
    if (options.length > 0) {
      m_Env = Environments.readEnv(options[0]);
      if (m_Env == null)
        throw new InvalidEnvironmentException(options[0]);
      options[0] = "";
    }
    else {
      throw new InvalidEnvironmentException();
    }
  }

  /**
   * Sets the environment.
   *
   * @param value	the environment
   */
  public void setEnv(Environment value) {
    m_Env = value;
  }

  /**
   * Returns the environment, if any.
   *
   * @return		the environment, null if none set
   */
  public Environment getEnv() {
    return m_Env;
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  public ArgumentParser getParser() {
    return null;
  }

  /**
   * Returns whether the command utilizes additional arguments that get passed on.
   *
   * @return		true if additional options
   */
  public boolean supportsAdditionalArguments() {
    return false;
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  protected abstract boolean doExecute(Namespace ns, String[] options);

  /**
   * Executes the command.
   *
   * @param options 	the arguments for the command
   * @return		true if successful
   */
  public boolean execute(String[] options) {
    ArgumentParser	parser;
    Namespace ns;

    parser = getParser();
    ns     = null;
    if (parser != null) {
      try {
	ns = parser.parseArgs(options, true);
      }
      catch (ArgumentParserException e) {
	parser.handleError(e);
	return false;
      }
    }

    return doExecute(ns, supportsAdditionalArguments() ? options : new String[0]);
  }

  /**
   * Simply uses the command for comparing.
   *
   * @param o		the other command to compare with
   * @return		less than, equal to, or greater than zero
   * 			if the name is less than, equal to or greater
   */
  @Override
  public int compareTo(AbstractCommand o) {
    return getName().compareTo(o.getName());
  }

  /**
   * Checks whether the object is a command and has the same name.
   *
   * @param obj		the object to compare with
   * @return		true if the same command
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractCommand) && (compareTo((AbstractCommand) obj) == 0);
  }

  /**
   * Lists all available commands.
   *
   * @return		the commands
   */
  public static List<AbstractCommand> getCommands() {
    List<AbstractCommand>	result;
    List<Class>			classes;
    AbstractCommand		cmd;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      AbstractCommand.class,
      new String[]{AbstractCommand.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
        cmd = (AbstractCommand) cls.newInstance();
        result.add(cmd);
      }
      catch (Exception e) {
        // ignored
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Removes any empty strings from the array.
   *
   * @param options 	the options to compress
   * @return		the compressed options
   */
  public static String[] compress(String[] options) {
    List<String>	result;
    int			i;

    result = new ArrayList<>();
    for (i = 0; i < options.length; i++) {
      if (!options[i].isEmpty())
	result.add(options[i]);
    }

    return result.toArray(new String[result.size()]);
  }
}
