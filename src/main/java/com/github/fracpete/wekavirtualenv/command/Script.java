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
import com.github.fracpete.wekavirtualenv.command.script.Variables;
import com.github.fracpete.wekavirtualenv.command.script.VariablesHandler;
import com.github.fracpete.wekavirtualenv.command.script.instructions.Block;
import com.github.fracpete.wekavirtualenv.command.script.instructions.Engine;
import com.github.fracpete.wekavirtualenv.command.script.instructions.EngineContext;
import com.github.fracpete.wekavirtualenv.core.Destroyable;
import com.github.fracpete.wekavirtualenv.core.InvalidIndentationException;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Just outputs a message.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Script
  extends AbstractCommandWithOutputListeners
  implements VariablesHandler, EngineContext, Destroyable {

  /** whether we are in verbose mode. */
  protected boolean m_Verbose;

  /** the variables. */
  protected Variables m_Variables;

  /** the engine for executing the commands. */
  protected Engine m_Engine;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    m_Engine = null;
  }

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
      + "Empty lines and lines starting with " + Block.COMMENT + " get skipped.";
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
   * Returns the variables.
   *
   * @return		the variables
   */
  public Variables getVariables() {
    return m_Variables;
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
    Block		instructions;

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
      addError("Failed to load commands from: " + scriptfile, e);
      return false;
    }

    m_Verbose = ns.getBoolean("verbose");
    m_Variables = new Variables();

    try {
      instructions = Block.parse(cmds);
      m_Engine = new Engine(this, instructions, m_Verbose, m_OutputListeners);
      return m_Engine.execute();
    }
    catch (InvalidIndentationException e) {
      addError("Failed to parse instructions!", e);
      return false;
    }
    finally {
      m_OutputListeners.clear();
      m_Engine = null;
    }
  }

  /**
   * Destroys the process if possible.
   */
  public void destroy() {
    if (m_Engine != null)
      m_Engine.destroy();
  }
}
