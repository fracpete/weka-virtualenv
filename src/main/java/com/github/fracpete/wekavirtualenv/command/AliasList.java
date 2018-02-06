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
 * AliasList.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.env.Aliases;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists alias definitions (names and associated options).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class AliasList
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "alias-list";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return
      "Lists aliases and their associated options.\n"
      + "Listing can be for global aliases, per environment, or for all.";
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
    result.addOption("--env")
      .dest("env")
      .help("the name of the environment, if environment-specific alias")
      .setDefault("")
      .required(false);
    result.addOption("--all")
      .dest("all")
      .help("for listing all alias definitions")
      .argument(false)
      .required(false);

    return result;
  }

  /**
   * Right-pads the string.
   *
   * @param s		the string to pad
   * @param width	the width for the string
   * @param c		the character to use for padding
   * @return		the padded string
   */
  protected String rightPad(String s, int width, char c) {
    StringBuilder	result;

    result = new StringBuilder(s);
    while (result.length() < width)
      result.append(c);

    return result.toString();
  }

  /**
   * Generates nice output from the header and definitions.
   *
   * @param header	the header
   * @param definitions	the definitions
   * @return		the generated string
   */
  protected String toString(String header, List<String> definitions) {
    StringBuilder	result;
    StringBuilder	separator;
    int			cols;
    int[]		width;
    String[]		parts;
    int			i;

    result = new StringBuilder();

    // determine widths
    cols  = header.split("\t").length;
    width = new int[cols];
    parts = header.split("\t");
    for (i = 0; i < parts.length; i++)
      width[i] = parts[i].length();
    for (String def: definitions) {
      parts = def.split("\t");
      for (i = 0; i < parts.length; i++)
	width[i] = Math.max(width[i], parts[i].length());
    }

    // generate output
    // header
    parts     = header.split("\t");
    separator = new StringBuilder();
    for (i = 0; i < parts.length; i++) {
      if (i > 0) {
	result.append(" | ");
	separator.append("-+-");
      }
      result.append(rightPad(parts[i], width[i], ' '));
      separator.append(rightPad("", width[i], '-'));
    }
    result.append("\n");
    result.append(separator.toString());
    result.append("\n");

    // data
    for (String def: definitions) {
      parts = def.split("\t");
      for (i = 0; i < parts.length; i++) {
	if (i > 0)
	  result.append(" | ");
	result.append(rightPad(parts[i], width[i], ' '));
      }
      result.append("\n");
    }

    return result.toString();
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
    String		header;
    List<String>	definitions;
    List<String>	errors;

    // all?
    errors = new ArrayList<>();
    if (ns.getBoolean("all")) {
      header      = "Environment\tName\tCommand";
      definitions = Aliases.listAll(errors);
    }
    else {
      header = "Name\tCommand";
      // global?
      if (ns.getString("env").isEmpty())
        definitions = Aliases.list(null, errors);
      else
        definitions = Aliases.list(ns.getString("env"), errors);
    }

    if (!errors.isEmpty()) {
      for (String error: errors)
        addError(error);
      return false;
    }
    else if (definitions == null) {
      addError("Failed to retrieve alias definitions!");
      return false;
    }
    else {
      println(toString(header, definitions), true);
    }

    return true;
  }
}
