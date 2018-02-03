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
 * ReadLines.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Reads all the lines in a file.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ReadLines
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "read_lines";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Reads all the lines in a text file into a variable.\n"
      + "Can skip empty lines and lines that match regular expression.";
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
      .help("the text file to read.")
      .required(true);
    result.addOption("--skip-empty")
      .dest("skipempty")
      .help("whether to skip empty lines")
      .argument(false);
    result.addOption("--regexp")
      .dest("regexp")
      .help("the regular expression to use for keeping lines.")
      .setDefault(".*");
    result.addOption("--invert-matching")
      .dest("invertmatching")
      .help("whether to invert the matching sense of the regular expression")
      .argument(false);
    result.addOption("--dest")
      .dest("dest")
      .help("the name of the var to store the lines in.")
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
    File		file;
    String 		regexp;
    Pattern 		pattern;
    boolean  		skipEmpty;
    boolean		invertMatching;
    List<String>	lines;
    int			i;
    boolean		keep;

    file = new File(ns.getString("file"));
    if (!file.exists()) {
      addError("File does not exist: " + file);
      return false;
    }
    if (file.isDirectory()) {
      addError("File points to a directory: " + file);
      return false;
    }

    skipEmpty      = ns.getBoolean("skipempty");
    invertMatching = ns.getBoolean("invertmatching");

    pattern = null;
    regexp  = ns.getString("regexp");
    if (!regexp.equals(".*")) {
      try {
        pattern = Pattern.compile(regexp);
      }
      catch (Exception e) {
        addError("Failed to parse regular expression: " + regexp, e);
        return false;
      }
    }

    // read
    try {
      lines = Files.readAllLines(file.toPath());
    }
    catch (Exception e) {
      addError("Failed to read: " + file, e);
      return false;
    }

    // process
    i = 0;
    while (i < lines.size()) {
      keep = true;
      if (skipEmpty && lines.get(i).trim().isEmpty()) {
        keep = false;
      }
      if ((pattern != null)) {
        if (!invertMatching && !pattern.matcher(lines.get(i)).matches())
          keep = false;
        else if (invertMatching && pattern.matcher(lines.get(i)).matches())
          keep = false;
      }
      if (!keep)
        lines.remove(i);
      else
	i++;
    }

    getContext().getVariables().set(ns.getString("dest"), lines.toArray(new String[lines.size()]));

    return true;
  }
}
