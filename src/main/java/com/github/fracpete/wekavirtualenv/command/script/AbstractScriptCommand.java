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
 * AbstractScriptCommand.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command.script;

import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.command.AbstractCommand;
import com.github.fracpete.wekavirtualenv.command.Script;
import com.github.fracpete.wekavirtualenv.command.script.instructions.EngineContext;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for a command that can only be run within a script.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @see Script
 */
public abstract class AbstractScriptCommand
  extends AbstractCommand
  implements ScriptCommand {

  /** the script this command belongs to. */
  protected EngineContext m_Context;

  /**
   * Sets the script context.
   *
   * @param value	the context
   */
  public void setContext(EngineContext value) {
    m_Context = value;
  }

  /**
   * Returns the script context.
   *
   * @return		the context, null if none set
   */
  public EngineContext getContext() {
    return m_Context;
  }

  /**
   * Evaluates the script command.
   *
   * @param ns		the namespace
   * @param options	the options
   * @return		true if successful
   */
  protected abstract boolean evalCommand(Namespace ns, String[] options);

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns, String[] options) {
    if (m_Context == null) {
      addError("No script context set!");
      return false;
    }
    return evalCommand(ns, options);
  }

  /**
   * Lists all available script commands.
   *
   * @return		the commands
   */
  public static List<ScriptCommand> getScriptCommands() {
    List<ScriptCommand>		result;
    List<Class>			classes;
    ScriptCommand		cmd;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      ScriptCommand.class,
      new String[]{ScriptCommand.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
	cmd = (ScriptCommand) cls.newInstance();
	result.add(cmd);
      }
      catch (Exception e) {
	// ignored
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Returns the command associated with the command name.
   *
   * @param name	the name of the command
   * @return		the command, null if not available
   */
  public static ScriptCommand getScriptCommand(String name) {
    ScriptCommand	result;

    result = null;
    for (ScriptCommand cmd: getScriptCommands()) {
      if (cmd.getName().equals(name)) {
        result = cmd;
        break;
      }
    }

    return result;
  }
}
