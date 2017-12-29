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
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.action;

import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ancestor for actions.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractAction
  implements Comparable<AbstractAction> {

  /** the image directory. */
  public final static String IMAGE_DIR = "com/github/fracpete/wekavirtualenv/gui/images/";

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
   * Returns the swing action for the menu.
   *
   * @return		the action
   */
  public javax.swing.AbstractAction getAction() {
    javax.swing.AbstractAction  result;
    ImageIcon			icon;

    result = new javax.swing.AbstractAction() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String msg = execute();
        if (msg != null)
          GUIHelper.showErrorMessage(null, "Failed to execute " + getName() + ":\n" + msg);
      }
    };
    result.putValue(javax.swing.AbstractAction.NAME, getName());
    icon = GUIHelper.getIcon(IMAGE_DIR + getClass().getName());
    if (icon != null)
      result.putValue(javax.swing.AbstractAction.SMALL_ICON, icon);

    return result;
  }

  /**
   * Hook method for checking before executing the action.
   *
   * @return		null if successful, otherwise error message
   */
  protected String check() {
    return null;
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
    AbstractAction		cmd;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      AbstractAction.class,
      new String[]{AbstractAction.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
        cmd = (AbstractAction) cls.newInstance();
        result.add(cmd);
      }
      catch (Exception e) {
        // ignored
      }
    }

    Collections.sort(result);

    return result;
  }
}
