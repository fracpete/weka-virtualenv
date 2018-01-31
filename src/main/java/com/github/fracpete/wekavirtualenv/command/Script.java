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
 * Script.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.script.AbstractScriptCommand;
import com.github.fracpete.wekavirtualenv.core.InvalidEnvironmentException;
import com.github.fracpete.wekavirtualenv.core.MissingEnvironmentException;
import nz.ac.waikato.cms.core.Utils;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Just outputs a message.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Script
  extends AbstractCommand {

  /** the line comment start. */
  public final static String COMMENT = "#";

  /** the opening of a variable. */
  public final static String VAR_START = "${";

  /** the closing of a variable. */
  public final static String VAR_END = "}";

  /** whether we are in verbose mode. */
  protected boolean m_Verbose;

  /** the variables. */
  protected Map<String,String> m_Variables;

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "script";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Executes the commands in the specified script file.\n"
      + "Empty lines and lines starting with " + COMMENT + " get skipped.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		always null
   */
  @Override
  public ArgumentParser getParser() {
    ArgumentParser 	result;

    result = new ArgumentParser(getName());
    result.addOption("--file")
      .dest("file")
      .help("the script file to execute")
      .required(true);
    result.addOption("--verbose")
      .dest("verbose")
      .help("in verbose mode, commands to be executed are output on stderr")
      .argument(false);

    return result;
  }

  /**
   * Removes all empty and comment lines.
   *
   * @param cmds	the commands to clean in-place
   */
  protected void cleanCommands(List<String> cmds) {
    int		i;

    i = 0;
    while (i < cmds.size()) {
      if (cmds.get(i).trim().isEmpty()) {
        cmds.remove(i);
        continue;
      }
      if (cmds.get(i).trim().startsWith(COMMENT)) {
        cmds.remove(i);
        continue;
      }
      i++;
    }
  }

  /**
   * Sets the variable and its value. If the value is null, the variable
   * gets removed
   *
   * @param name	the name of the variable
   * @param value	the value, null to remove variable
   */
  public void setVariable(String name, String value) {
    if (value == null)
      m_Variables.remove(name);
    else
      m_Variables.put(name, value);
  }

  /**
   * Checks whether the variable exists.
   *
   * @param name	the variable to check
   * @return		true if present
   */
  public boolean hasVariable(String name) {
    return m_Variables.containsKey(name);
  }

  /**
   * Returns the value of the variable.
   *
   * @param name	the name of the variable to retrieve
   * @return		the value, null if variable doesn't exist
   */
  public String getVariable(String name) {
    return m_Variables.get(name);
  }

  /**
   * Returns the names of the currently stored variables.
   *
   * @return		the variable names
   */
  public List<String> variableNames() {
    List<String>	result;

    result = new ArrayList<>(m_Variables.keySet());
    Collections.sort(result);

    return result;
  }

  /**
   * Expands all variables in the command.
   *
   * @param cmd		the command to process
   * @return		the processed command
   */
  public String expandVariables(String cmd) {
    String	result;

    result = cmd;

    for (String name: m_Variables.keySet())
      result = result.replace(VAR_START + name + VAR_END, m_Variables.get(name));

    if (m_Verbose)
      System.err.println("");

    return result;
  }

  /**
   * Configures the command setup for the script.
   *
   * @param setup	the setup to update
   * @return		the command, null if failed to configure
   */
  public boolean configureScriptSetup(CommandSetup setup) {
    for (AbstractCommand c: AbstractCommand.getCommands()) {
      if (c.getName().equals(setup.options[0])) {
	setup.command = c;
	break;
      }
    }
    // check script commands
    if (setup.command == null) {
      for (AbstractScriptCommand c: AbstractScriptCommand.getScriptCommands()) {
	if (c.getName().equals(setup.options[0])) {
	  setup.command = c;
	  break;
	}
      }
    }
    if (setup.command == null) {
      System.err.println("Unknown command: " + setup.options[0]);
      new Help().execute(new String[0]);
      return false;
    }

    // remove command from array
    setup.options[0] = "";
    setup.options = AbstractCommand.compress(setup.options);

    // check for help
    for (String option: setup.options) {
      if (option.equals("--help")) {
        System.out.println(setup.command.generateHelpScreen(true, true));
	return true;
      }
    }

    // environment name?
    if (setup.command.requiresEnvironment()) {
      try {
	setup.command.loadEnv(setup.options);
	setup.options = AbstractCommand.compress(setup.options);
      }
      catch (MissingEnvironmentException e) {
        System.err.println("No environment supplied!");
        System.out.println(setup.command.generateHelpScreen(false, true));
	return false;
      }
      catch (InvalidEnvironmentException ie) {
        System.err.println("Invalid environment supplied: " + (setup.options[0]));
        new ListEnvs().execute(new String[0]);
	return false;
      }
    }

    if (setup.command instanceof AbstractScriptCommand)
      ((AbstractScriptCommand) setup.command).setContext(this);

    return true;
  }

  /**
   * Processes the command.
   *
   * @param cmd		the command to execute
   * @return		true if successfully executed
   */
  protected boolean processCommand(String cmd) {
    CommandSetup	setup;

    if (m_Verbose)
      System.err.println("[RAW] " + cmd);
    cmd = expandVariables(cmd);
    if (m_Verbose)
      System.err.println("[EXP] " + cmd);

    try {
      setup = new CommandSetup();
      setup.options = OptionUtils.splitOptions(cmd);
      if (!configureScriptSetup(setup) || (setup.command == null))
	return false;

      // execute
      return executeSetup(setup);
    }
    catch (Exception e) {
      addError("Failed to execute command: " + cmd + "\n" + Utils.throwableToString(e));
      return false;
    }
  }

  /**
   * Processes the commands.
   *
   * @param cmds	the commands to execute
   * @return		true if successfully executed
   */
  protected boolean processCommands(List<String> cmds) {
    boolean	result;

    m_Variables = new HashMap<>();

    result = true;
    for (String cmd: cmds) {
      result = processCommand(cmd);
      if (!result)
        break;
    }

    return result;
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns, String[] options) {
    File 		scriptfile;
    List<String>	cmds;

    scriptfile = new File(ns.getString("file"));
    if (!scriptfile.exists()) {
      addError("Script file does not exist: " + scriptfile);
      return false;
    }
    if (scriptfile.isDirectory()) {
      addError("Script file points to a directory: " + scriptfile);
      return false;
    }

    try {
      cmds = Files.readAllLines(scriptfile.toPath());
    }
    catch (Exception e) {
      addError("Failed to load commands from: " + scriptfile + "\n" + Utils.throwableToString(e));
      return false;
    }

    m_Verbose = ns.getBoolean("verbose");

    cleanCommands(cmds);
    return processCommands(cmds);
  }
}
