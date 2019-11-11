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
 * WekaVirtualEnv.java
 * Copyright (C) 2017-2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui;

import com.github.fracpete.wekavirtualenv.gui.command.AbstractGUICommand;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import com.github.fracpete.wekavirtualenv.gui.core.Settings;
import com.github.fracpete.wekavirtualenv.gui.core.Stoppable;
import com.github.fracpete.wekavirtualenv.gui.env.EnvironmentsPanel;
import nz.ac.waikato.cms.gui.core.BaseFrame;
import nz.ac.waikato.cms.gui.core.BasePanel;
import nz.ac.waikato.cms.gui.core.GUIHelper;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;

/**
 * Main gui for managing virtual environments for Weka.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WekaVirtualEnv
  extends BasePanel {

  /** the divider panel. */
  protected JSplitPane m_SplitPane;

  /** the environments. */
  protected EnvironmentsPanel m_PanelEnvs;

  /** the tabbed pane for the outputs. */
  protected JTabbedPane m_TabbedPaneOutputs;

  /** the menuitem whether to show compact or normal view. */
  protected JCheckBoxMenuItem m_MenuItemCompact;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_SplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setResizeWeight(0.0);
    add(m_SplitPane);

    m_TabbedPaneOutputs = new JTabbedPane();
    m_SplitPane.setRightComponent(m_TabbedPaneOutputs);

    m_PanelEnvs = new EnvironmentsPanel();
    m_PanelEnvs.setButtonsPanelVisible(false);
    m_PanelEnvs.setTabbedPane(m_TabbedPaneOutputs);
    m_SplitPane.setLeftComponent(m_PanelEnvs);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    restoreSettings();
  }

  /**
   * Sets the divider location (environments | output) at the specified location.
   *
   * @param location	the location in pixels from the left
   */
  public void setDividerLocation(int location) {
    m_SplitPane.setDividerLocation(location);
  }

  /**
   * Returns the menu bar to use.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar			result;
    JMenu			menu;
    JMenuItem			menuitem;
    JCheckBoxMenuItem		checkmenuitem;

    result = new JMenuBar();

    menu = new JMenu("Environments");
    menu.setMnemonic('E');
    menu.addChangeListener((ChangeEvent e) -> updateMenu());
    result.add(menu);

    for (AbstractGUICommand cmd: AbstractGUICommand.getCommands()) {
      if (cmd.requiresEnvironment())
        continue;
      cmd.setTabbedPane(m_TabbedPaneOutputs);
      cmd.setEnvironmentsPanel(m_PanelEnvs);
      menuitem = new JMenuItem(cmd.getAction());
      menu.add(menuitem);
    }

    checkmenuitem = new JCheckBoxMenuItem("Compact", IconHelper.getEmptyIcon());
    checkmenuitem.addActionListener((ActionEvent e) -> m_PanelEnvs.setCompactView(m_MenuItemCompact.isSelected()));
    menu.add(checkmenuitem);
    m_MenuItemCompact = checkmenuitem;

    menuitem = new JMenuItem("Reload", IconHelper.getIcon("Reload"));
    menuitem.addActionListener((ActionEvent e) -> m_PanelEnvs.reload());
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Exit", IconHelper.getIcon("Close"));
    menuitem.addActionListener((ActionEvent e) -> close());
    menu.add(menuitem);

    menu = new JMenu("Install");
    menu.addChangeListener((ChangeEvent e) -> updateMenu());
    menu.setMnemonic('I');
    result.add(menu);

    menuitem = new JMenuItem("Proxy");
    menuitem.addActionListener((ActionEvent e) -> m_PanelEnvs.manageProxy());
    menu.add(menuitem);

    menu.addSeparator();

    menuitem = new JMenuItem("Update");
    menuitem.addActionListener((ActionEvent e) -> m_PanelEnvs.updateVersions());
    menu.add(menuitem);

    menuitem = new JMenuItem("Download");
    menuitem.addActionListener((ActionEvent e) -> m_PanelEnvs.downloadVersion());
    menu.add(menuitem);

    return result;
  }

  /**
   * Updates the menu.
   */
  protected void updateMenu() {
    m_MenuItemCompact.setSelected(m_PanelEnvs.isCompactView());
  }

  /**
   * Restores the previous settings.
   */
  public void restoreSettings() {
    Properties	settings;
    Frame	frame;

    settings = Settings.load();
    m_PanelEnvs.setCompactView(settings.getProperty("compact", "false").equals("true"));
    if (settings.getProperty("divider") != null)
      m_SplitPane.setDividerLocation(Integer.parseInt(settings.getProperty("divider")));
    frame = GUIHelper.getParentFrame(this);
    if (frame != null) {
      if ((settings.getProperty("x") != null) && (settings.getProperty("y") != null))
	frame.setLocation(Integer.parseInt(settings.getProperty("x")), Integer.parseInt(settings.getProperty("y")));
      if ((settings.getProperty("width") != null) && (settings.getProperty("height") != null))
	frame.setSize(Integer.parseInt(settings.getProperty("width")), Integer.parseInt(settings.getProperty("height")));
    }
  }

  /**
   * Stores the current settings.
   */
  protected void storeSettings() {
    Properties	settings;
    Frame	frame;

    settings = new Properties();
    settings.setProperty("compact", "" + m_PanelEnvs.isCompactView());
    settings.setProperty("divider", "" + m_SplitPane.getDividerLocation());
    frame = GUIHelper.getParentFrame(this);
    if (frame != null) {
      settings.setProperty("x", "" + frame.getX());
      settings.setProperty("y", "" + frame.getY());
      settings.setProperty("width", "" + frame.getWidth());
      settings.setProperty("height", "" + frame.getHeight());
    }
    Settings.save(settings);
  }

  /**
   * Closes the application.
   */
  public void close() {
    int			i;

    for (i = m_TabbedPaneOutputs.getTabCount() - 1; i >= 0; i--) {
      if (m_TabbedPaneOutputs.getComponentAt(i) instanceof Stoppable)
	((Stoppable) m_TabbedPaneOutputs.getComponentAt(i)).stop();
      m_TabbedPaneOutputs.removeTabAt(i);
    }

    storeSettings();

    GUIHelper.closeParent(this);
  }

  /**
   * Displays the GUI.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    final WekaVirtualEnv panel = new WekaVirtualEnv();
    BaseFrame frame = new BaseFrame("Weka virtualenv");
    frame.setIconImage(IconHelper.getIcon("wenv").getImage());
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.setJMenuBar(panel.getMenuBar());
    frame.setSize(new Dimension(1200, 800));
    panel.setDividerLocation(400);
    frame.setLocationRelativeTo(null);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        panel.close();
      }
    });
    panel.restoreSettings();
    frame.setVisible(true);
  }
}
