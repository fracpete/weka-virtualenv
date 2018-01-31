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
 * ListDirs.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Locates directories in specified directory and stores them in a variable.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ListDirs
  extends AbstractScriptCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "list_dirs";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  @Override
  public String getHelp() {
    return "Locates directories in specified directory and stores them in a variable.\n"
      + "Search can be recursive, directory names (excl parent path) can be matched again regular expression.";
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
    result.addOption("--dir")
      .dest("dir")
      .help("the directory to search.")
      .required(true);
    result.addOption("--recursive")
      .dest("recursive")
      .help("whether to descend into sub-directories")
      .argument(false);
    result.addOption("--regexp")
      .dest("regexp")
      .help("the regular expression to match directory names against.")
      .setDefault(".*");
    result.addOption("--var")
      .dest("var")
      .help("the name of the var to store the result in.")
      .required(true);

    return result;
  }

  /**
   * Searches the specified directory and adds the matching dirs to the list.
   *
   * @param dir		the directory to search
   * @param recursive	true if to search recursively
   * @param regexp	the pattern to match against, null to match all
   * @param dirs	the list of dirs to add matches to
   */
  protected void search(File dir, boolean recursive, Pattern regexp, List<String> dirs) {
    File[]	current;

    current = dir.listFiles();
    for (File file: current) {
      if (file.getName().equals(".") || file.getName().equals(".."))
        continue;

      // go deeper?
      if (file.isDirectory()) {
        if (regexp == null)
          dirs.add(file.getAbsolutePath());
        else if (regexp.matcher(file.getName()).matches())
          dirs.add(file.getAbsolutePath());

        if (recursive)
          search(file, recursive, regexp, dirs);
      }
    }
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
    File		dir;
    List<String>	files;
    String 		regexp;
    Pattern 		pattern;
    boolean		recursive;

    files = new ArrayList<>();
    dir   = new File(ns.getString("dir"));
    if (!dir.exists()) {
      addError("Search directory does not exist: " + dir);
      return false;
    }
    if (!dir.isDirectory()) {
      addError("Search directory does not point to a directory: " + dir);
      return false;
    }

    recursive = ns.getBoolean("recursive");

    pattern = null;
    regexp  = ns.getString("regexp");
    if (!regexp.equals(".*")) {
      try {
        pattern = Pattern.compile(regexp);
      }
      catch (Exception e) {
        addError("Failed to parse regular expression: " + regexp);
        return false;
      }
    }

    search(dir, recursive, pattern, files);

    getContext().setVariable(ns.getString("var"), files.toArray(new String[files.size()]));

    return true;
  }
}
