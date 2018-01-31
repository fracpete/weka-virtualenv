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
 * Command.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.wekavirtualenv.env.Environment;

/**
 * Interface for virtual environment commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface Command
  extends Comparable<Command> {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  public String getName();

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp();

  /**
   * Generates a help screen.
   *
   * @param outputParser 	whether to output the help from the parser as well
   */
  public String generateHelpScreen(boolean requested, boolean outputParser);

  /**
   * Returns whether it requires an environment.
   *
   * @return		true if required
   */
  public boolean requiresEnvironment();

  /**
   * Returns whether any errors were recorded.
   *
   * @return		true if errors present
   */
  public boolean hasErrors();

  /**
   * Returns the errors.
   *
   * @return		the errors, null if none present
   */
  public String getErrors();

  /**
   * Hook method for loading the environment.
   * <br>
   * Instantiates the environment from the first parameter and removes this
   * parameter.
   *
   * @param options	the options to parse
   */
  public void loadEnv(String[] options);

  /**
   * Sets the environment.
   *
   * @param value	the environment
   */
  public void setEnv(Environment value);

  /**
   * Returns the environment, if any.
   *
   * @return		the environment, null if none set
   */
  public Environment getEnv();

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  public ArgumentParser getParser();

  /**
   * Returns whether the command utilizes additional arguments that get passed on.
   *
   * @return		true if additional options
   */
  public boolean supportsAdditionalArguments();

  /**
   * Executes the command.
   *
   * @param options 	the arguments for the command
   * @return		true if successful
   */
  public boolean execute(String[] options);

  /**
   * Simply uses the command for comparing.
   *
   * @param o		the other command to compare with
   * @return		less than, equal to, or greater than zero
   * 			if the name is less than, equal to or greater
   */
  @Override
  public int compareTo(Command o);

  /**
   * Checks whether the object is a command and has the same name.
   *
   * @param obj		the object to compare with
   * @return		true if the same command
   */
  @Override
  public boolean equals(Object obj);
}
