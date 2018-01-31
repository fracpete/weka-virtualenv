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
 * Calc.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import org.mariuszgromada.math.mxparser.Expression;

/**
 * For calculating the result of a mathematical expression.
 * Uses <a href="http://mathparser.org/">mXparser</a>.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Calc
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "calc";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Calculates the result of a mathematical expression.";
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
    result.addOption("--expr")
      .dest("expr")
      .help("the expression to calculate.")
      .required(true);
    result.addOption("--var")
      .dest("var")
      .help("the name of the var to store the result in.")
      .required(true);

    return result;
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
    Expression		expr;

    expr = new Expression(ns.getString("expr"));
    getContext().setVariable(ns.getString("var"), "" + expr.calculate());

    return true;
  }
}
