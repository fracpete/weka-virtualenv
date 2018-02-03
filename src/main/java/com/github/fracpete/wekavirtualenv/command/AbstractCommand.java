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
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.ArgumentParserException;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.filter.AbstractFilter;
import com.github.fracpete.wekavirtualenv.command.filter.FilterSetup;
import com.github.fracpete.wekavirtualenv.core.InvalidEnvironmentException;
import com.github.fracpete.wekavirtualenv.core.MissingEnvironmentException;
import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import nz.ac.waikato.cms.core.Utils;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;
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
  implements Command {

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
   * Generates a help screen.
   *
   * @param outputParser 	whether to output the help from the parser as well
   */
  public String generateHelpScreen(boolean requested, boolean outputParser) {
    StringBuilder	result;
    ArgumentParser	parser;

    result = new StringBuilder();
    if (requested) {
      result.append("Help requested");
      result.append("\n\n");
    }

    result.append(getName() + (requiresEnvironment() ? " <env>" : "")
      + (getParser() != null ? " <options>" : "")
      + (supportsAdditionalArguments() ? " <args>" : "")
      + (this instanceof CommandWithFilterSupport ? " | output filter(s)" : "")+ "\n");

    for (String line: getHelp().split("\n"))
      result.append("\t").append(line).append("\n");

    parser = getParser();
    if (outputParser && (parser != null)) {
      result.append("\n");
      result.append(parser.generateHelpScreen(false, false, false, true));
    }

    return result.toString();
  }

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
   * @param t 		the associated exception
   */
  public void addError(String msg, Throwable t) {
    addError(msg + "\n" + Utils.throwableToString(t));
  }

  /**
   * Stores and outputs the error message.
   *
   * @param msg		the message
   */
  public void addError(String msg) {
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
      throw new MissingEnvironmentException();
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
	return !parser.getHelpRequested();
      }
    }

    if (!supportsAdditionalArguments()) {
      options = CommandUtils.compress(options);
      if (OptionUtils.joinOptions(options).trim().length() > 0)
        System.err.println("Unparsed options ('" + getName() + "' does not pass on any options): " + OptionUtils.joinOptions(options));
      options = new String[0];
    }

    return doExecute(ns, options);
  }

  /**
   * Simply uses the command for comparing.
   *
   * @param o		the other command to compare with
   * @return		less than, equal to, or greater than zero
   * 			if the name is less than, equal to or greater
   */
  @Override
  public int compareTo(Command o) {
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
    return (obj instanceof Command) && (compareTo((Command) obj) == 0);
  }

  /**
   * Lists all available commands.
   *
   * @return		the commands
   */
  public static List<Command> getCommands() {
    List<Command>	result;
    List<Class>		classes;
    Command		cmd;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      Command.class,
      new String[]{Command.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
	cmd = (Command) cls.newInstance();
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
   * Returns the command associated with the command name.
   *
   * @param name	the name of the command
   * @return		the command, null if not available
   */
  public static Command getCommand(String name) {
    Command		result;

    result = null;
    for (Command cmd: getCommands()) {
      if (cmd.getName().equals(name)) {
        result = cmd;
        break;
      }
    }

    return result;
  }

  /**
   * Adds the filter to the command.
   *
   * @param setup	the setup to add the filter to
   * @param exit	whether to allow System.exit
   * @param filterArgs	the filter arguments
   * @return		true if successfully added
   */
  protected static boolean addFilter(CommandSetup setup, boolean exit, List<String> filterArgs) {
    FilterSetup		filterSetup;

    filterSetup = new FilterSetup();
    filterSetup.options = filterArgs.toArray(new String[filterArgs.size()]);
    if (!AbstractFilter.configure(filterSetup)) {
      System.err.println("Failed to configure filter: " + OptionUtils.joinOptions(filterArgs.toArray(new String[filterArgs.size()])));
      if (exit)
	System.exit(1);
      else
	return false;
    }
    else {
      if (setup.command instanceof CommandWithFilterSupport) {
	((CommandWithFilterSupport) setup.command).addFilter(filterSetup.filter);
      }
      else {
	System.err.println("Command '" + setup.command.getName() + "' does not support filters!");
	if (exit)
	  System.exit(1);
	else
	  return false;
      }
    }

    filterArgs.clear();

    return true;
  }

  /**
   * Configures the command setup.
   *
   * @param setup	the setup to update
   * @param exit	whether System.exit is allowed
   * @return		the command, null if failed to configure
   */
  public static boolean configureSetup(CommandSetup setup, boolean exit) {
    List<String>	filterArgs;
    int			firstFilterPos;
    int			i;

    setup.command = AbstractCommand.getCommand(setup.options[0]);
    if (setup.command == null) {
      System.err.println("Unknown command: " + setup.options[0]);
      new Help().execute(new String[0]);
      if (exit)
	System.exit(1);
      else
	return false;
    }

    // remove command from array
    setup.options[0] = "";
    setup.options = CommandUtils.compress(setup.options);

    // check for help
    for (String option: setup.options) {
      if (option.equals("--help")) {
        System.out.println(setup.command.generateHelpScreen(true, true));
        if (exit)
          System.exit(0);
        else
          return true;
      }
    }

    // environment name?
    if (setup.command.requiresEnvironment()) {
      try {
	setup.command.loadEnv(setup.options);
	setup.options = CommandUtils.compress(setup.options);
      }
      catch (MissingEnvironmentException e) {
        System.err.println("No environment supplied!");
        System.out.println(setup.command.generateHelpScreen(false, true));
        if (exit)
          System.exit(1);
        else
          return false;
      }
      catch (InvalidEnvironmentException ie) {
        System.err.println("Invalid environment supplied: " + (setup.options[0]));
        new ListEnvs().execute(new String[0]);
        if (exit)
          System.exit(1);
        else
          return false;
      }
    }

    // filters?
    firstFilterPos = -1;
    filterArgs     = new ArrayList<>();
    for (i = 0; i < setup.options.length; i++) {
      if (setup.options[i].equals("|")) {
        if (filterArgs.size() > 0) {
          if (!addFilter(setup, exit, filterArgs))
            return false;
	}
        if (firstFilterPos == -1)
	  firstFilterPos = i;
	filterArgs = new ArrayList<>();
        continue;
      }
      if (firstFilterPos > -1)
	filterArgs.add(setup.options[i]);
    }
    if (firstFilterPos > -1) {
      if (filterArgs.size() > 0) {
	if (!addFilter(setup, exit, filterArgs))
	  return false;
      }
      setup.options = CommandUtils.removeFrom(setup.options, firstFilterPos);
    }

    return true;
  }

  /**
   * Executes the command setup.
   *
   * @param setup	the setup
   * @return		true if successfully executed
   */
  public static boolean executeSetup(CommandSetup setup) {
    boolean	success;

    success = setup.command.execute(setup.options);
    if (!success) {
      if (setup.command.hasErrors())
	System.err.println(setup.command.getErrors());
      else
	System.err.println("Failed to execute command!");
      return false;
    }

    return true;
  }

  /**
   * Parses the command-line options and executes the command if possible.
   *
   * @param args	the options to use
   * @param exit	if system exits are allowed
   * @return		true if successful
   */
  public static boolean parseArgs(String[] args, boolean exit) {
    CommandSetup 	setup;

    // output help if no options supplied
    if (args.length == 0) {
      new Help().execute(new String[0]);
      if (exit)
	System.exit(0);
      else
	return true;
    }

    // locate command
    setup = new CommandSetup();
    setup.options = args.clone();
    if (!configureSetup(setup, exit) || (setup.command == null))
      return false;

    // execute
    return executeSetup(setup);
  }
}
