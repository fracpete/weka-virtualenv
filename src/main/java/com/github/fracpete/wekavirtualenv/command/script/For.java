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
 * For.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.simpleargparse4j.Option.Type;
import com.github.fracpete.wekavirtualenv.command.script.instructions.Block;
import com.github.fracpete.wekavirtualenv.command.script.instructions.Engine;

/**
 * Good ole for loop.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class For
  extends AbstractScriptCommand
  implements InstructionBlockHandler, IteratingScriptCommand {

  /** the instructions to execute with each element. */
  protected Block m_Instructions;

  /** the variable to store the current element under. */
  protected String m_Variable;

  /** the current amount. */
  protected double m_Current;

  /** the upper bound. */
  protected double m_Upper;

  /** the step amount. */
  protected double m_Step;

  /** whether we are in verbose mode. */
  protected boolean m_Verbose;

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "for";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Iterates through the numeric values from lower to upper bound, using the specified step amount.";
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
    result.addOption("--from")
      .dest("from")
      .type(Type.DOUBLE)
      .help("the lower bound (inclusive).")
      .required(true);
    result.addOption("--to")
      .dest("to")
      .type(Type.DOUBLE)
      .help("the upper bound (exclusive).")
      .required(true);
    result.addOption("--step")
      .dest("step")
      .type(Type.INTEGER)
      .help("the step amount for each iteration.")
      .setDefault(1);
    result.addOption("--dest")
      .dest("dest")
      .help("the name of the var to store current value under.")
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
    return (m_Current + m_Step < m_Upper);
  }

  /**
   * Performs the next iteration.
   *
   * @return		true if successfully executed
   */
  @Override
  public boolean iterate() {
    Engine	engine;
    String	current;

    m_Current += m_Step;
    if ((int) m_Current == m_Current)
      current = "" + (int) m_Current;
    else
      current = "" + m_Current;
    getVariables().set(m_Variable, current);
    if (m_Verbose)
      System.err.println("[FOR] " + m_Current);
    engine = new Engine(m_Context, m_Instructions, m_Verbose);
    return engine.execute();
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

    m_Step     = ns.getDouble("step");
    m_Upper    = ns.getDouble("to");
    m_Current  = ns.getDouble("from") - m_Step;
    m_Verbose  = ns.getBoolean("verbose");
    m_Variable = ns.getString("dest");

    result = true;

    while (canIterate()) {
      result = iterate();
      if (!result)
        break;
    }

    return result;
  }
}
