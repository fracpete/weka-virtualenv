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
 * ActionOutputPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import com.github.fracpete.wekavirtualenv.command.OutputListener;
import com.github.fracpete.wekavirtualenv.gui.core.ScrollPane;
import nz.ac.waikato.cms.gui.core.BasePanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * For capturing output from an action.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ActionOutputPanel
  extends BasePanel
  implements OutputListener {

  /** the tabbed pane this panel belongs to. */
  protected JTabbedPane m_TabbedPane;

  /** the action that generated the output. */
  protected EnvironmentAction m_Action;

  /** the text area. */
  protected JTextArea m_TextArea;

  /** the button for closing the output. */
  protected JButton m_ButtonClose;

  /**
   * Initializes the widgets;
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());
    m_TextArea = new JTextArea();
    add(new ScrollPane(m_TextArea));

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonClose = new JButton("Close");
    m_ButtonClose.addActionListener((ActionEvent e) -> close());
    panel.add(m_ButtonClose);
  }

  /**
   * Sets the owning tabbed pane.
   *
   * @param value	the owner
   */
  public void setTabbedPane(JTabbedPane value) {
    m_TabbedPane = value;
  }

  /**
   * Returns the owning tabbed pane.
   *
   * @return		the owner
   */
  public JTabbedPane getTabbedPane() {
    return m_TabbedPane;
  }

  /**
   * Sets the action that generated the output.
   *
   * @param value	the action
   */
  public void setAction(EnvironmentAction value) {
    m_Action = value;
  }

  /**
   * Returns the action that generated the output.
   *
   * @return		the action
   */
  public EnvironmentAction getAction() {
    return m_Action;
  }

  /**
   * Gets called when output was produced.
   *
   * @param line	the line to process
   * @param stdout	whether stdout or stderr
   */
  public void outputOccurred(String line, boolean stdout) {
    m_TextArea.append((stdout ? "[OUT] " : "[ERR] ") + line + "\n");
  }

  /**
   * Stops the process.
   */
  protected void stop() {
    m_Action.getOwner().destroy();
  }

  /**
   * Removes itself from the tabbed pane.
   */
  protected void close() {
    stop();
    m_TabbedPane.remove(this);
  }
}
