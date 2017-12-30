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
 * EnvironmentAction.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.gui.action.AbstractAction;
import com.github.fracpete.wekavirtualenv.gui.action.AbstractEnvironmentAction;
import nz.ac.waikato.cms.gui.core.GUIHelper;

import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import java.awt.event.ActionEvent;

/**
 * Actions for environments.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EnvironmentAction
  extends javax.swing.AbstractAction {

  /** the owner. */
  protected AbstractAction m_Owner;

  /** the tabbed pane for output. */
  protected JTabbedPane m_TabbedPane;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(AbstractAction value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public AbstractAction getOwner() {
    return m_Owner;
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
   * Sets the environment.
   *
   * @param env		the environment to use
   */
  public void setEnvironment(Environment env) {
    if (m_Owner instanceof AbstractEnvironmentAction)
      ((AbstractEnvironmentAction) m_Owner).setEnvironment(env);
  }

  /**
   * Returns whether the action generates console output.
   *
   * @return		true if the action generates console output
   */
  public boolean generatesOutput() {
    return m_Owner.generatesOutput();
  }

  /**
   * Executes the action.
   *
   * @param e		the event
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    ActionOutputPanel	panel;
    SwingWorker		worker;

    if (m_Owner.generatesOutput()) {
      panel = new ActionOutputPanel();
      panel.setOwner(m_TabbedPane);
      m_TabbedPane.addTab(m_Owner.getName(), panel);  // TODO increment counter?
      m_Owner.addOutputListener(panel);
    }

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	String msg = m_Owner.execute();
	if (msg != null)
	  GUIHelper.showErrorMessage(null, "Failed to execute " + m_Owner.getName() + ":\n" + msg);
	return null;
      }
    };
    worker.execute();
  }
}
