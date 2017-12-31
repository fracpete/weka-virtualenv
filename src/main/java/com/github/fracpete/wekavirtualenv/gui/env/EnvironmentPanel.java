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
 * EnvironmentPanel.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.gui.action.AbstractAction;
import com.github.fracpete.wekavirtualenv.gui.action.GUIChooser;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import nz.ac.waikato.cms.gui.core.BasePanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays an environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EnvironmentPanel
  extends BasePanel {

  /** the owner. */
  protected EnvironmentsPanel m_Owner;

  /** the underlying environment. */
  protected Environment m_Environment;

  /** the name. */
  protected JLabel m_LabelName;

  /** the java executable to use. */
  protected JLabel m_LabelJava;

  /** the memory to use. */
  protected JLabel m_LabelMemory;

  /** the weka jar. */
  protected JLabel m_LabelWeka;

  /** the prefix labels .*/
  protected List<JLabel> m_Prefixes;

  /** the button for the actions. */
  protected JButton m_ButtonActions;

  /** the button for the guichooser. */
  protected JButton m_ButtonGUIChooser;

  /** the action menu. */
  protected JPopupMenu m_ActionMenu;

  /** the menu items. */
  protected List<EnvironmentAction> m_Actions;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    String		group;
    EnvironmentAction 	envaction;

    super.initialize();

    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    m_Environment = null;
    m_Actions = new ArrayList<>();
    group         = "";
    for (AbstractAction action: AbstractAction.getActions()) {
      if (!group.equals(action.getGroup()))
        m_Actions.add(null);
      envaction = action.getAction();
      m_Actions.add(envaction);
      group = action.getGroup();
    }

    m_Prefixes = new ArrayList<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    Font	font;

    super.initGUI();

    setLayout(new BorderLayout());

    // labels
    panel = new JPanel(new GridLayout(4, 1));
    add(panel, BorderLayout.CENTER);

    m_LabelName = new JLabel();
    m_LabelName.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    font = m_LabelName.getFont();
    font = font.deriveFont(Font.BOLD);
    font = font.deriveFont(Math.round(font.getSize() * 1.1));
    m_LabelName.setFont(font);
    panel.add(m_LabelName);

    m_LabelJava = new JLabel();
    panel.add(createEntry("Java", m_LabelJava));

    m_LabelMemory = new JLabel();
    panel.add(createEntry("Memory", m_LabelMemory));

    m_LabelWeka = new JLabel();
    panel.add(createEntry("Weka", m_LabelWeka));

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonActions = new JButton("...");
    m_ButtonActions.addActionListener((ActionEvent ae) -> showActions());
    panel.add(m_ButtonActions);

    m_ButtonGUIChooser = new JButton(IconHelper.getIcon("GUIChooser"));
    m_ButtonGUIChooser.addActionListener((ActionEvent ae) -> startGuichooser());
    panel.add(m_ButtonGUIChooser);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    int		width;

    super.finishInit();

    width = 0;
    for (JLabel prefix: m_Prefixes)
      width = Math.max(width, prefix.getPreferredSize().width);

    for (JLabel prefix: m_Prefixes)
      prefix.setPreferredSize(new Dimension(width, prefix.getPreferredSize().height));
  }

  /**
   * Generates a panel with an entry.
   *
   * @param prefix	the prefix
   * @param label	the label
   * @return		the generated panel
   */
  protected JPanel createEntry(String prefix, JLabel label) {
    JPanel	result;
    JLabel	labelPrefix;
    Font	font;

    result = new JPanel(new FlowLayout(FlowLayout.LEFT));

    labelPrefix = new JLabel(prefix);
    font = labelPrefix.getFont();
    font = font.deriveFont(Font.PLAIN);
    labelPrefix.setFont(font);
    m_Prefixes.add(labelPrefix);
    result.add(labelPrefix);
    result.add(label);

    return result;
  }

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(EnvironmentsPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public EnvironmentsPanel getOwner() {
    return m_Owner;
  }

  /**
   * Sets the environment to use.
   *
   * @param value	the environment
   */
  public void setEnvironment(Environment value) {
    m_Environment = value;
    updateLabels();
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
   * Updates the labels using the current environment object.
   */
  protected void updateLabels() {
    if (m_Environment == null) {
      m_LabelName.setText("");
      m_LabelJava.setText(Environment.DEFAULT);
      m_LabelMemory.setText(Environment.DEFAULT);
      m_LabelWeka.setText("");
    }
    else {
      m_LabelName.setText(m_Environment.name);
      m_LabelJava.setText(m_Environment.java.isEmpty() ? Environment.DEFAULT : m_Environment.java);
      m_LabelMemory.setText(m_Environment.memory.isEmpty() ? Environment.DEFAULT : m_Environment.memory);
      m_LabelWeka.setText(m_Environment.weka);
    }
  }

  /**
   * Shows the action menu.
   */
  protected void showActions() {
    JPopupMenu menu;

    if (m_ActionMenu != null) {
      menu = m_ActionMenu;
    }
    else {
      menu = new JPopupMenu();
      for (EnvironmentAction action : m_Actions) {
        if (action == null) {
	  menu.addSeparator();
	}
        else {
          action.setEnvironment(getEnvironment());
          action.setEnvironmentsPanel(getOwner());
	  action.setTabbedPane(getOwner().getTabbedPane());
	  menu.add(action);
	}
      }
    }
    menu.show(this, 0, this.getHeight());
  }

  /**
   * Starts the GUIChooser.
   */
  protected void startGuichooser() {
    EnvironmentAction	envaction;
    GUIChooser		action;

    action    = new GUIChooser();
    action.setEnvironment(getEnvironment());
    envaction = new EnvironmentAction();
    envaction.setEnvironmentsPanel(getOwner());
    envaction.setTabbedPane(getOwner().getTabbedPane());
    envaction.setEnvironment(getEnvironment());
    envaction.setOwner(action);
    envaction.actionPerformed(null);
  }
}
