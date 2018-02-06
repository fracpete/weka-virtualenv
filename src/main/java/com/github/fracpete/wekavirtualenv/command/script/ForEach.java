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
 * ForEach.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.script.instructions.Block;
import com.github.fracpete.wekavirtualenv.command.script.instructions.Engine;
import com.github.fracpete.wekavirtualenv.core.Destroyable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Iterates through the elements of a variable and executes the nested instructions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ForEach
  extends AbstractScriptCommand
  implements InstructionBlockHandler, IteratingScriptCommand, Destroyable {

  /** the instructions to execute with each element. */
  protected Block m_Instructions;

  /** the elements to iterate. */
  protected List<String> m_Elements;

  /** the variable to store the current element under. */
  protected String m_Variable;

  /** whether we are in verbose mode. */
  protected boolean m_Verbose;

  /** whether the iteration got stopped. */
  protected boolean m_Stopped;

  /** the execution of the current iteration. */
  protected Engine m_Current;

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "foreach";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Iterates through the elements of a variable and executes the nested instructions.";
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
    result.addOption("--iterate")
      .dest("iterate")
      .help("the variable to iterate.")
      .required(true);
    result.addOption("--dest")
      .dest("dest")
      .help("the name of the variable to store the result in.")
      .required(true);
    result.addOption("--verbose")
      .dest("verbose")
      .help("in verbose mode, commands to be executed are output on stderr")
      .argument(false);

    return result;
  }

  /**
   * Sets the instructions.
   *
   * @param instructions	the commands to execute
   */
  @Override
  public void setInstructions(Block instructions) {
    m_Instructions = instructions;
  }

  /**
   * For querying whether the command can still iterate.
   *
   * @return		true if to iterate, false if finished
   */
  @Override
  public boolean canIterate() {
    return !m_Stopped && (m_Elements.size() > 0);
  }

  /**
   * Performs the next iteration.
   *
   * @return		true if successfully executed
   */
  @Override
  public boolean iterate() {
    boolean	result;
    String	next;

    next = m_Elements.remove(0);
    getVariables().set(m_Variable, next);
    if (m_Verbose)
      println("[FOREACH] " + next, false);
    m_Current = new Engine(m_Context, m_Instructions, m_Verbose, m_OutputListeners);
    result    = m_Current.execute();
    m_Current = null;
    return result;
  }

  /**
   * Returns the managed variables.
   *
   * @return		the variables
   */
  @Override
  public Variables getVariables() {
    return m_Context.getVariables();
  }

  /**
   * Evaluates the script command.
   *
   * @param ns		the namespace
   * @param options	the options
   * @return		true if successful
   */
  @Override
  protected boolean evalCommand(Namespace ns, String[] options) {
    boolean	result;
    Object	var;

    m_Elements = new ArrayList<>();
    var = getVariables().get(ns.getString("iterate"));
    if (var != null) {
      if (var instanceof String)
        m_Elements.add((String) var);
      else
        m_Elements.addAll(Arrays.asList((String[]) var));
    }
    else {
      addError("Variable not present for iteration: " + ns.getString("iterate"));
      return false;
    }

    m_Verbose = ns.getBoolean("verbose");
    m_Variable = ns.getString("dest");

    result = true;

    while (canIterate()) {
      result = iterate();
      if (!result)
        break;
    }

    return result;
  }

  /**
   * Destroys the process if possible.
   */
  public void destroy() {
    m_Stopped = true;
    if (m_Current != null)
      m_Current.destroy();
  }
}
