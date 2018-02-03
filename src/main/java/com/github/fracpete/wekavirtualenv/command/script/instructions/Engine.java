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
 * Execution.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script.instructions;

import com.github.fracpete.wekavirtualenv.command.AbstractCommand;
import com.github.fracpete.wekavirtualenv.command.Command;
import com.github.fracpete.wekavirtualenv.command.CommandSetup;
import com.github.fracpete.wekavirtualenv.command.CommandUtils;
import com.github.fracpete.wekavirtualenv.command.Help;
import com.github.fracpete.wekavirtualenv.command.ListEnvs;
import com.github.fracpete.wekavirtualenv.command.script.AbstractScriptCommand;
import com.github.fracpete.wekavirtualenv.command.script.InstructionBlockHandler;
import com.github.fracpete.wekavirtualenv.command.script.ScriptCommand;
import com.github.fracpete.wekavirtualenv.core.InvalidEnvironmentException;
import com.github.fracpete.wekavirtualenv.core.MissingEnvironmentException;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

/**
 * Executes commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Engine {

  /** the context. */
  protected EngineContext m_Context;

  /** the instructions to execute. */
  protected Block m_Instructions;

  /** whether we are in verbose mode. */
  protected boolean m_Verbose;

  /**
   * Initializes the engine.
   *
   * @param context		the context
   * @param instructions	the instructions to execute
   * @param verbose		whether to use verbose mode
   */
  public Engine(EngineContext context, Block instructions, boolean verbose) {
    m_Context      = context;
    m_Instructions = instructions;
    m_Verbose      = verbose;
  }

  /**
   * Returns the verbose state.
   *
   * @return		true if to use verbose output
   */
  public boolean isVerbose() {
    return m_Verbose;
  }

  /**
   * Configures the command setup for the script.
   *
   * @param setup	the setup to update
   * @return		the command, null if failed to configure
   */
  public boolean configureScriptSetup(CommandSetup setup) {
    for (Command c: AbstractCommand.getCommands()) {
      if (c.getName().equals(setup.options[0])) {
	setup.command = c;
	break;
      }
    }
    // check script commands
    if (setup.command == null) {
      for (ScriptCommand c: AbstractScriptCommand.getScriptCommands()) {
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
    setup.options = CommandUtils.compress(setup.options);

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
	setup.options = CommandUtils.compress(setup.options);
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
      ((AbstractScriptCommand) setup.command).setContext(m_Context);

    return true;
  }

  /**
   * Executes the command.
   *
   * @param cmd		the command to execute
   * @param block	the nested block for the command, if any
   * @return		true if successfully executed
   */
  protected boolean execute(String cmd, Block block) {
    CommandSetup setup;

    if (m_Verbose)
      System.err.println("[RAW] " + cmd);
    cmd = m_Context.getVariables().expand(cmd);
    if (m_Verbose)
      System.err.println("[EXP] " + cmd);

    try {
      setup = new CommandSetup();
      setup.options = OptionUtils.splitOptions(cmd);
      if (!configureScriptSetup(setup) || (setup.command == null))
	return false;
      if ((setup.command instanceof InstructionBlockHandler) && (block != null))
	((InstructionBlockHandler) setup.command).setInstructions(block);

      // execute
      return AbstractCommand.executeSetup(setup);
    }
    catch (Exception e) {
      m_Context.addError("Failed to execute command: " + cmd, e);
      return false;
    }
  }

  /**
   * Executes the instructions.
   *
   * @return		true if successfully executed
   */
  public boolean execute() {
    boolean	result;
    int		i;
    Block	block;
    Instruction	instruction;

    result = true;

    i = 0;
    while (i < m_Instructions.size()) {
      instruction = m_Instructions.get(i);
      block       = null;
      if (i < m_Instructions.size() - 1) {
        if (m_Instructions.get(i + 1) instanceof Block)
          block = (Block) m_Instructions.get(i + 1);
      }
      if (instruction instanceof Line) {
	result = execute(((Line) instruction).getInstruction(), block);
	if (block != null)
	  i++;
      }
      else {
        m_Context.addError("Expected command, but found nested block!");
        result = false;
        break;
      }
      i++;
    }

    return result;
  }
}