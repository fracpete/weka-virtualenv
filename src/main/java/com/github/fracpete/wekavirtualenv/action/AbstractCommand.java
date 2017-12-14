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

package com.github.fracpete.wekavirtualenv.action;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractCommand
  implements Comparable<AbstractCommand> {

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
   * Returns the parser to use for the arguments.
   *
   * @return		the parser, null if no arguments to parse
   */
  protected abstract ArgumentParser getParser();

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @return		true if successful
   */
  protected abstract boolean doExecute(Namespace ns);

  /**
   * Executes the command.
   *
   * @param options 	the arguments for the command
   * @return		true if successful
   */
  public boolean execute(String[] options) {
    ArgumentParser	parser;
    Namespace		ns;

    parser = getParser();
    ns     = null;
    if (parser != null) {
      try {
	ns = parser.parseArgs(options);
      }
      catch (ArgumentParserException e) {
	parser.handleError(e);
	return false;
      }
    }

    return doExecute(ns);
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
}
