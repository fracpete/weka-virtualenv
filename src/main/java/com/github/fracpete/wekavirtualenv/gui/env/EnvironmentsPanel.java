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
 * EnvironmentsPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import nz.ac.waikato.cms.gui.core.BasePanel;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

/**
 * Lists all the panels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EnvironmentsPanel
  extends BasePanel {

  /** the tabbed pane for output. */
  protected JTabbedPane m_TabbedPane;

  /** the button for creating a new environment. */
  protected JButton m_ButtonCreate;

  /** the button for reloading the environments. */
  protected JButton m_ButtonReload;

  /** the panel . */
  protected JPanel m_PanelAll;

  /** the environments. */
  protected JPanel m_PanelEnvs;

  /** the scroll pane. */
  protected JScrollPane m_ScrollPaneEnvs;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelAll = new JPanel(new BorderLayout());
    m_PanelEnvs = new JPanel(new GridLayout(0, 1, 5, 5));
    m_PanelAll.add(m_PanelEnvs, BorderLayout.NORTH);
    m_ScrollPaneEnvs = new JScrollPane(m_PanelAll);
    add(m_ScrollPaneEnvs, BorderLayout.CENTER);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.SOUTH);
    m_ButtonCreate = new JButton(IconHelper.getIcon("Create"));
    m_ButtonCreate.addActionListener((ActionEvent e) -> create());
    panel.add(m_ButtonCreate);

    m_ButtonReload = new JButton(IconHelper.getIcon("Reload"));
    m_ButtonReload.addActionListener((ActionEvent e) -> reload());
    panel.add(m_ButtonReload);
  }

  /**
   * Finishes up the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    reload();
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
   * Creates an environment.
   */
  protected void create() {
    // TODO
  }

  /**
   * Reloads all environments.
   */
  protected void reload() {
    EnvironmentPanel	panel;

    m_PanelEnvs.removeAll();

    for (Environment env: Environments.list()) {
      panel = new EnvironmentPanel();
      panel.setEnvironment(env);
      panel.setOwner(this);
      m_PanelEnvs.add(panel);
    }

    invalidate();
    revalidate();
  }
}
