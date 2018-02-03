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
 * FilterHelp.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.filter.AbstractFilter;
import com.github.fracpete.wekavirtualenv.command.filter.Filter;

/**
 * Outputs help on the output filters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FilterHelp
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "filter_help";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Prints help on the available output filters.";
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
    result.addOption("--filter")
      .dest("filter")
      .help("the specific filter to output the help for.")
      .required(false);

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
    if (ns.getString("filter").isEmpty()) {
      System.out.println("Available filters:\n");
      for (Filter f : AbstractFilter.getFilters())
        System.out.println(f.generateHelpScreen(false, false));
    }
    else {
      Filter f = AbstractFilter.getFilter(ns.getString("filter"));
      if (f == null) {
        System.err.println("Unknown filter: " + ns.getString("filter"));
        return false;
      }
      System.out.println(f.getParser().generateHelpScreen(false));
    }
    System.out.println();
    System.out.println("Notes:");
    System.out.println("<options>");
    System.out.println("\tthe command supports additional options,");
    System.out.println("\tspecify the filter's name to output detailed help.");

    return true;
  }
}
