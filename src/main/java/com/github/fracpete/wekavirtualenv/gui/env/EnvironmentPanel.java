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
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.gui.command.AbstractGUICommand;
import com.github.fracpete.wekavirtualenv.gui.command.GUIChooser;
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

  /** whether to show compact or normal view. */
  protected boolean m_CompactView;

  /** the full layout. */
  protected JPanel m_NormalPanel;

  /** the name. */
  protected JLabel m_NormalLabelName;

  /** the java executable to use. */
  protected JLabel m_NormalLabelJava;

  /** the memory to use. */
  protected JLabel m_NormalLabelMemory;

  /** the weka jar. */
  protected JLabel m_NormalLabelWeka;

  /** the Environment vars. */
  protected JLabel m_NormalLabelEnvVars;

  /** whether package manager is offline. */
  protected JLabel m_NormalLabelPkgMgrOffline;

  /** the prefix labels .*/
  protected List<JLabel> m_NormalPrefixes;

  /** the button for the actions. */
  protected JButton m_NormalButtonActions;

  /** the button for the guichooser. */
  protected JButton m_NormalButtonGUIChooser;

  /** the action menu. */
  protected JPopupMenu m_ActionMenu;

  /** the compact layout. */
  protected JPanel m_CompactPanel;

  /** the name. */
  protected JLabel m_CompactLabelName;

  /** the button for the actions. */
  protected JButton m_CompactButtonActions;

  /** the button for the guichooser. */
  protected JButton m_CompactButtonGUIChooser;

  /** the menu items. */
  protected List<AbstractGUICommand> m_Commands;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    String		group;

    super.initialize();

    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    m_CompactView   = false;
    m_Environment   = null;
    m_Commands      = new ArrayList<>();
    group           = "";
    for (AbstractGUICommand cmd : AbstractGUICommand.getCommands()) {
      if (!cmd.requiresEnvironment())
        continue;
      if (!group.equals(cmd.getGroup()))
        m_Commands.add(null);
      m_Commands.add(cmd);
      group = cmd.getGroup();
    }

    m_NormalPrefixes = new ArrayList<>();
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

    // normal view
    m_NormalPanel = new JPanel(new BorderLayout(0, 0));
    add(m_NormalPanel, BorderLayout.CENTER);

    // labels
    panel = new JPanel(new GridLayout(0, 1));
    m_NormalPanel.add(panel, BorderLayout.CENTER);

    m_NormalLabelName = new JLabel();
    m_NormalLabelName.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    font = m_NormalLabelName.getFont();
    font = font.deriveFont(Font.BOLD);
    font = font.deriveFont(Math.round(font.getSize() * 1.1));
    m_NormalLabelName.setFont(font);
    panel.add(m_NormalLabelName);

    m_NormalLabelJava = new JLabel();
    panel.add(createEntry("Java", m_NormalLabelJava));

    m_NormalLabelMemory = new JLabel();
    panel.add(createEntry("Memory", m_NormalLabelMemory));

    m_NormalLabelWeka = new JLabel();
    panel.add(createEntry("Weka", m_NormalLabelWeka));

    m_NormalLabelEnvVars = new JLabel();
    panel.add(createEntry("Env. vars", m_NormalLabelEnvVars));

    m_NormalLabelPkgMgrOffline = new JLabel();
    panel.add(createEntry("PkgMgr offline", m_NormalLabelPkgMgrOffline));

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_NormalPanel.add(panel, BorderLayout.SOUTH);

    m_NormalButtonActions = new JButton("...");
    m_NormalButtonActions.addActionListener((ActionEvent ae) -> showActions(m_NormalButtonActions));
    panel.add(m_NormalButtonActions);

    m_NormalButtonGUIChooser = new JButton(IconHelper.getIcon("GUIChooser"));
    m_NormalButtonGUIChooser.addActionListener((ActionEvent ae) -> startGuichooser());
    panel.add(m_NormalButtonGUIChooser);

    // compact view
    m_CompactPanel = new JPanel(new BorderLayout(0, 0));

    m_CompactLabelName = new JLabel();
    m_CompactLabelName.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    font = m_CompactLabelName.getFont();
    font = font.deriveFont(Font.BOLD);
    font = font.deriveFont(Math.round(font.getSize() * 1.1));
    m_CompactLabelName.setFont(font);
    m_CompactPanel.add(m_CompactLabelName, BorderLayout.WEST);

    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_CompactPanel.add(panel, BorderLayout.EAST);

    m_CompactButtonActions = new JButton("...");
    m_CompactButtonActions.addActionListener((ActionEvent ae) -> showActions(m_CompactButtonActions));
    panel.add(m_CompactButtonActions);

    m_CompactButtonGUIChooser = new JButton(IconHelper.getIcon("GUIChooser"));
    m_CompactButtonGUIChooser.addActionListener((ActionEvent ae) -> startGuichooser());
    panel.add(m_CompactButtonGUIChooser);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    int		width;

    super.finishInit();

    width = 0;
    for (JLabel prefix: m_NormalPrefixes)
      width = Math.max(width, prefix.getPreferredSize().width);

    for (JLabel prefix: m_NormalPrefixes)
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
    m_NormalPrefixes.add(labelPrefix);
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
   * Turns characters (<>&) into HTML entities.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String toHTML(String s) {
    return s
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;");
  }

  /**
   * Updates the labels using the current environment object.
   */
  protected void updateLabels() {
    if (m_Environment == null) {
      m_NormalLabelName.setText("");
      m_NormalLabelJava.setText(Environment.DEFAULT);
      m_NormalLabelMemory.setText(Environment.DEFAULT);
      m_NormalLabelWeka.setText("");
      m_NormalLabelEnvVars.setText("");
      m_NormalLabelPkgMgrOffline.setText("no");
      m_CompactLabelName.setText("");
      m_CompactLabelName.setToolTipText("");
      m_CompactPanel.setToolTipText(m_CompactLabelName.getToolTipText());
    }
    else {
      m_NormalLabelName.setText(m_Environment.name);
      m_NormalLabelJava.setText(m_Environment.java.isEmpty() ? Environment.DEFAULT : m_Environment.java);
      m_NormalLabelMemory.setText(m_Environment.memory.isEmpty() ? Environment.DEFAULT : m_Environment.memory);
      m_NormalLabelWeka.setText(m_Environment.weka);
      m_NormalLabelEnvVars.setText(m_Environment.envvars.isEmpty() ? Environment.NONE : m_Environment.envvars);
      m_NormalLabelPkgMgrOffline.setText(m_Environment.pkgMgrOffline ? "yes" : "no");
      m_CompactLabelName.setText(m_Environment.name);
      m_CompactLabelName.setToolTipText(
        "<html>"
	  + "Java: " + toHTML(m_NormalLabelJava.getText()) + "<br>"
	  + "Memory: " + toHTML(m_NormalLabelMemory.getText()) + "<br>"
	  + "Weka: " + toHTML(m_NormalLabelWeka.getText()) + "<br>"
	  + "EnvVars: " + toHTML(m_NormalLabelEnvVars.getText()) + "<br>"
	  + "PkgMgr offline: " + toHTML(m_NormalLabelPkgMgrOffline.getText()) + "<br>"
	  + "</html>");
      m_CompactPanel.setToolTipText(m_CompactLabelName.getToolTipText());
    }
  }

  /**
   * Shows the action menu.
   */
  protected void showActions(JButton button) {
    JPopupMenu menu;

    if (m_ActionMenu != null) {
      menu = m_ActionMenu;
    }
    else {
      menu = new JPopupMenu();
      for (AbstractGUICommand cmd : m_Commands) {
        if (cmd == null) {
	  menu.addSeparator();
	}
        else {
          cmd.setEnvironment(getEnvironment());
          cmd.setEnvironmentsPanel(getOwner());
	  cmd.setTabbedPane(getOwner().getTabbedPane());
	  menu.add(cmd.getAction());
	}
      }
      m_ActionMenu = menu;
    }
    menu.show(button, 0, button.getHeight());
  }

  /**
   * Starts the GUIChooser.
   */
  protected void startGuichooser() {
    GUIChooser		action;

    action = new GUIChooser();
    action.setEnvironment(getEnvironment());
    action.setEnvironmentsPanel(getOwner());
    action.setTabbedPane(getOwner().getTabbedPane());
    action.setEnvironment(getEnvironment());
    action.getAction().actionPerformed(null);
  }

  /**
   * Sets whether to show a compact view.
   *
   * @param value	true if to show compact
   */
  public void setCompactView(boolean value) {
    m_CompactView = value;
    if (m_CompactView) {
      remove(m_NormalPanel);
      add(m_CompactPanel, BorderLayout.CENTER);
    }
    else {
      remove(m_CompactPanel);
      add(m_NormalPanel, BorderLayout.CENTER);
    }
    invalidate();
    revalidate();
    doLayout();
    repaint();
  }

  /**
   * Returns whether the compact view is shown.
   *
   * @return		true if compact shown
   */
  public boolean isCompactView() {
    return m_CompactView;
  }
}
