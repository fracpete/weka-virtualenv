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
 * AbstractGUICommand.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.command;

import com.github.fracpete.wekavirtualenv.command.AbstractLaunchCommand;
import com.github.fracpete.wekavirtualenv.command.OutputListener;
import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import com.github.fracpete.wekavirtualenv.gui.env.ActionOutputPanel;
import com.github.fracpete.wekavirtualenv.gui.env.EnvironmentsPanel;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ancestor for GUI commands.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractGUICommand
  implements Comparable<AbstractGUICommand> {

  /** the key for the owner. */
  public final static String KEY_OWNER = "Owner";

  /** the output listeners. */
  protected Set<OutputListener> m_OutputListeners;

  /** the environment. */
  protected Environment m_Environment;

  /** the tabbed pane for output. */
  protected JTabbedPane m_TabbedPane;

  /** the environments panel. */
  protected EnvironmentsPanel m_EnvironmentsPanel;

  /** the launch counter. */
  protected Map<String,Integer> m_Counter;

  /**
   * Initializes the command.
   */
  public AbstractGUICommand() {
    super();
    m_OutputListeners   = new HashSet<>();
    m_TabbedPane        = null;
    m_EnvironmentsPanel = null;
    m_Counter           = new HashMap<>();
    m_Environment       = null;
  }

  /**
   * Adds the output listener.
   *
   * @param l		the listener
   */
  public void addOutputListener(OutputListener l) {
    m_OutputListeners.add(l);
  }

  /**
   * Removes the output listener.
   *
   * @param l		the listener
   */
  public void removeOutputListener(OutputListener l) {
    m_OutputListeners.remove(l);
  }

  /**
   * Sets the tabbed pane for output.
   *
   * @param value	the pane
   */
  public void setTabbedPane(JTabbedPane value) {
    m_TabbedPane = value;
  }

  /**
   * Returns the tabbed pane for output.
   *
   * @return		the pane
   */
  public JTabbedPane getTabbedPane() {
    return m_TabbedPane;
  }

  /**
   * Sets the environments panel.
   *
   * @param value	the panel
   */
  public void setEnvironmentsPanel(EnvironmentsPanel value) {
    m_EnvironmentsPanel = value;
  }

  /**
   * Returns the environments panel.
   *
   * @return		the panel
   */
  public EnvironmentsPanel getEnvironmentsPanel() {
    return m_EnvironmentsPanel;
  }

  /**
   * Sets the environment to use.
   *
   * @param value	the environment
   */
  public void setEnvironment(Environment value) {
    m_Environment = value;
  }

  /**
   * Returns the environment in use.
   *
   * @return		the environment, null if none set
   */
  public Environment getEnvironment() {
    return m_Environment;
  }

  /**
   * Returns the name of the command (displayed in GUI).
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * The group this command belongs to.
   *
   * @return		the group
   */
  public abstract String getGroup();

  /**
   * Returns whether the command is available.
   *
   * @return		true if available
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Returns the swing command for the menu.
   *
   * @return		the command
   */
  public AbstractAction getAction() {
    AbstractAction		result;
    ImageIcon			icon;
    final AbstractGUICommand	owner;

    owner  = this;
    result = new AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
	SwingWorker worker = new SwingWorker() {
	  protected String m_Error;
	  @Override
	  protected Object doInBackground() throws Exception {
	    m_Error = owner.execute();
	    return null;
	  }
	  @Override
	  protected void done() {
	    super.done();
	    if (m_Error != null)
	      GUIHelper.showErrorMessage(m_EnvironmentsPanel, m_Error);
	  }
	};
	worker.execute();
      }
    };
    icon = IconHelper.getIcon(getClass().getSimpleName());
    result.putValue(javax.swing.AbstractAction.NAME, getName());
    if (icon != null)
      result.putValue(javax.swing.AbstractAction.SMALL_ICON, icon);
    result.putValue(KEY_OWNER, this);
    result.setEnabled(isAvailable());

    return result;
  }

  /**
   * Returns whether the command requires an environment.
   *
   * @return		true if the command requires an environment
   */
  public abstract boolean requiresEnvironment();

  /**
   * Returns whether the command generates console output.
   *
   * @return		true if the command generates console output
   */
  public abstract boolean generatesOutput();

  /**
   * Hook method for checking before executing the command.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    if (requiresEnvironment() && (m_Environment == null))
      return "No environment provided!";
    return null;
  }

  /**
   * For transferring listeners.
   *
   * @param cmd		the command to receive the listeners
   */
  protected void transferOutputListeners(AbstractLaunchCommand cmd) {
    for (OutputListener l: m_OutputListeners)
      cmd.addOutputListener(l);
  }

  /**
   * Generates the tab name.
   *
   * @return		the tab name
   */
  protected String getTabName() {
    String	result;
    String	key;

    key = getEnvironment().name + ":" + getName();
    if (!m_Counter.containsKey(key))
      m_Counter.put(key, 0);
    m_Counter.put(key, m_Counter.get(key) + 1);

    result = key + " (" + m_Counter.get(key) + ")";

    return result;
  }
  /**
   * Performs the actual execution.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String doExecute();

  /**
   * Executes the command.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    ActionOutputPanel 	panel;
    String		result;

    result = check();
    if (result == null) {
      if (generatesOutput()) {
	panel = new ActionOutputPanel();
	panel.setTabbedPane(m_TabbedPane);
	panel.setCommand(this);
	m_TabbedPane.addTab(getTabName(), panel);
	addOutputListener(panel);
      }
      result = doExecute();
    }

    return result;
  }

  /**
   * Destroys the process if possible.
   * <br>
   * Default implementation does nothing.
   */
  public void destroy() {
  }

  /**
   * Uses the name for comparison.
   *
   * @param o		the other command to compare with
   * @return		the result of the string comparison of the groups/names
   */
  public int compareTo(AbstractGUICommand o) {
    int		result;

    result = getGroup().compareTo(o.getGroup());
    if (result == 0)
      result = getName().compareTo(o.getName());

    return result;
  }

  /**
   * Checks whether the object is a command with the same group/name.
   *
   * @param obj		the object to compare with
   * @return		true if command with same group/name
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractGUICommand) && (compareTo((AbstractGUICommand) obj) == 0);
  }

  /**
   * Lists all available commands.
   *
   * @return		the commands
   */
  public static List<AbstractGUICommand> getCommands() {
    List<AbstractGUICommand>	result;
    List<Class>			classes;
    AbstractGUICommand command;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      AbstractGUICommand.class,
      new String[]{AbstractGUICommand.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
	command = (AbstractGUICommand) cls.newInstance();
	result.add(command);
      }
      catch (Exception e) {
	// ignored
      }
    }

    Collections.sort(result);

    return result;
  }
}
