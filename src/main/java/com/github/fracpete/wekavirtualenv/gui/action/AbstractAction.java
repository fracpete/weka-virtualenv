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
 * AbstractAction.java
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.action;

import com.github.fracpete.wekavirtualenv.command.AbstractLaunchCommand;
import com.github.fracpete.wekavirtualenv.command.OutputListener;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import com.github.fracpete.wekavirtualenv.gui.env.EnvironmentAction;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ancestor for actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAction
  implements Comparable<AbstractAction> {

  /** the output listeners. */
  protected Set<OutputListener> m_OutputListeners;

  /**
   * Initializes the command.
   */
  public AbstractAction() {
    m_OutputListeners = new HashSet<>();
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
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * The group this action belongs to.
   *
   * @return		the group
   */
  public abstract String getGroup();

  /**
   * Returns whether the action is available.
   *
   * @return		true if available
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Returns the swing action for the menu.
   *
   * @return		the action
   */
  public EnvironmentAction getAction() {
    EnvironmentAction	result;
    ImageIcon		icon;

    result = new EnvironmentAction();
    result.setOwner(this);
    icon = IconHelper.getIcon(getClass().getSimpleName());
    result.putValue(javax.swing.AbstractAction.NAME, getName());
    if (icon != null)
      result.putValue(javax.swing.AbstractAction.SMALL_ICON, icon);

    return result;
  }

  /**
   * Returns whether the action generates console output.
   *
   * @return		true if the action generates console output
   */
  public abstract boolean generatesOutput();

  /**
   * Hook method for checking before executing the action.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
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
   * Performs the actual execution.
   *
   * @return		null if successful, otherwise error message
   */
  protected abstract String doExecute();

  /**
   * Executes the action.
   *
   * @return		null if successful, otherwise error message
   */
  public String execute() {
    String	result;

    result = check();
    if (result == null)
      result = doExecute();

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
   * @param o		the other action to compare with
   * @return		the result of the string comparison of the groups/names
   */
  public int compareTo(AbstractAction o) {
    int		result;

    result = getGroup().compareTo(o.getGroup());
    if (result == 0)
      result = getName().compareTo(o.getName());

    return result;
  }

  /**
   * Checks whether the object is an action with the same group/name.
   *
   * @param obj		the object to compare with
   * @return		true if action with same group/name
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractAction) && (compareTo((AbstractAction) obj) == 0);
  }

  /**
   * Lists all available actions.
   *
   * @return		the actions
   */
  public static List<AbstractAction> getActions() {
    List<AbstractAction>	result;
    List<Class>			classes;
    AbstractAction 		action;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      AbstractAction.class,
      new String[]{AbstractAction.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
        action = (AbstractAction) cls.newInstance();
        result.add(action);
      }
      catch (Exception e) {
        // ignored
      }
    }

    Collections.sort(result);

    return result;
  }
}
