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

import com.github.fracpete.wekavirtualenv.action.OutputListener;
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
  protected JTabbedPane m_Owner;

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
  public void setOwner(JTabbedPane value) {
    m_Owner = value;
  }

  /**
   * Returns the owning tabbed pane.
   *
   * @return		the owner
   */
  public JTabbedPane getOwner() {
    return m_Owner;
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
   * Removes itself from the tabbed pane.
   */
  protected void close() {
    // TODO stop process
    m_Owner.remove(this);
  }
}
