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
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import com.github.fracpete.inetutils4j.api.Proxy.ProxyType;
import com.github.fracpete.inetutils4j.core.DefaultCapture;
import com.github.fracpete.wekavirtualenv.command.Install;
import com.github.fracpete.wekavirtualenv.core.ProxyUtils;
import com.github.fracpete.wekavirtualenv.core.Versions;
import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import com.github.fracpete.wekavirtualenv.gui.command.Create;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import nz.ac.waikato.cms.core.Utils;
import nz.ac.waikato.cms.gui.core.ApprovalDialog;
import nz.ac.waikato.cms.gui.core.BasePanel;
import nz.ac.waikato.cms.gui.core.BaseScrollPane;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel.PropertyType;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
  protected BaseScrollPane m_ScrollPaneEnvs;

  /** the panel for the buttons. */
  protected JPanel m_PanelButtons;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelAll = new JPanel(new BorderLayout());
    m_PanelEnvs = new JPanel(new GridLayout(0, 1, 5, 5));
    m_PanelAll.add(m_PanelEnvs, BorderLayout.NORTH);
    m_ScrollPaneEnvs = new BaseScrollPane(m_PanelAll);
    add(m_ScrollPaneEnvs, BorderLayout.CENTER);

    // buttons
    m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    add(m_PanelButtons, BorderLayout.SOUTH);
    m_ButtonCreate = new JButton(IconHelper.getIcon("Create"));
    m_ButtonCreate.addActionListener((ActionEvent e) -> create());
    m_PanelButtons.add(m_ButtonCreate);

    m_ButtonReload = new JButton(IconHelper.getIcon("Reload"));
    m_ButtonReload.addActionListener((ActionEvent e) -> reload());
    m_PanelButtons.add(m_ButtonReload);
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
   * Sets whether the buttons panel is visible.
   *
   * @param value	true if visible
   */
  public void setButtonsPanelVisible(boolean value) {
    m_PanelButtons.setVisible(value);
  }

  /**
   * Returns whether the buttons panel is visible.
   *
   * @return		true if visible
   */
  public boolean isButtonsPanelVisible() {
    return m_PanelButtons.isVisible();
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
  public void create() {
    Create	create;
    String	msg;

    create = new Create();
    msg    = create.execute();
    reload();
    if (msg != null)
      GUIHelper.showErrorMessage(getParent(), msg);
  }

  /**
   * Reloads all environments.
   */
  public void reload() {
    EnvironmentPanel	panel;
    BasePanel 		panelNone;
    BasePanel		panelInfo;
    JLabel		labelNone;
    JButton		buttonCreate;
    List<Environment>	envs;

    m_PanelEnvs.removeAll();

    envs = Environments.list();
    if (envs.size() == 0) {
      panelNone = new BasePanel(new BorderLayout());
      panelInfo = new BasePanel(new FlowLayout(FlowLayout.CENTER));
      labelNone = new JLabel("No environment available");
      buttonCreate = new JButton("Create");
      buttonCreate.addActionListener((ActionEvent e) -> create());
      panelInfo.add(labelNone);
      panelInfo.add(buttonCreate);
      panelNone.add(panelInfo, BorderLayout.CENTER);
      m_PanelEnvs.add(panelNone);
    }
    else {
      for (Environment env : envs) {
	panel = new EnvironmentPanel();
	panel.setEnvironment(env);
	panel.setOwner(this);
	m_PanelEnvs.add(panel);
      }
    }

    invalidate();
    revalidate();
  }

  /**
   * Activates the proxy type if valid settings in props.
   *
   * @param type	the proxy to update
   * @param props	the properties to use
   */
  protected void setProxy(ProxyType type, Properties props) {
    String	hostKey;
    String	portKey;
    String	host;
    int		port;

    hostKey = type + ".host";
    portKey = type + ".port";
    if (props.containsKey(hostKey) && props.containsKey(portKey)) {
      host = props.getProperty(hostKey).trim();
      try {
        port = Integer.parseInt(props.getProperty(portKey).trim());
      }
      catch (Exception e) {
        port = -1;
      }
      if (!host.isEmpty() && (port > -1) && (port < 65536))
        ProxyUtils.setProxy(type, host, port);
      else
	ProxyUtils.unsetProxy(type);
    }
    else {
      ProxyUtils.unsetProxy(type);
    }
  }

  /**
   * For managing the proxy settings.
   */
  public void manageProxy() {
    ApprovalDialog		dialog;
    PropertiesParameterPanel	panel;
    Properties			props;
    
    panel = new PropertiesParameterPanel();
    props = new Properties();
    
    // http
    panel.addPropertyType(ProxyType.HTTP + ".host", PropertyType.STRING);
    panel.setLabel(ProxyType.HTTP + ".host", "Http - host");
    panel.setHelp(ProxyType.HTTP + ".host", "The URL of the proxy");
    props.setProperty(ProxyType.HTTP + ".host", ProxyUtils.getProxyHost(ProxyType.HTTP));
    panel.addPropertyType(ProxyType.HTTP + ".port", PropertyType.INTEGER);
    panel.setLabel(ProxyType.HTTP + ".port", "Http - port");
    panel.setHelp(ProxyType.HTTP + ".port", "The port of the proxy (0-65535)");
    props.setProperty(ProxyType.HTTP + ".port", "" + ProxyUtils.getProxyPort(ProxyType.HTTP));

    // ftp
    panel.addPropertyType(ProxyType.FTP + ".host", PropertyType.STRING);
    panel.setLabel(ProxyType.FTP + ".host", "Ftp - host");
    panel.setHelp(ProxyType.FTP + ".host", "The URL of the proxy");
    props.setProperty(ProxyType.FTP + ".host", ProxyUtils.getProxyHost(ProxyType.FTP));
    panel.addPropertyType(ProxyType.FTP + ".port", PropertyType.INTEGER);
    panel.setLabel(ProxyType.FTP + ".port", "Ftp - port");
    panel.setHelp(ProxyType.FTP + ".port", "The port of the proxy (0-65535)");
    props.setProperty(ProxyType.FTP + ".port", "" + ProxyUtils.getProxyPort(ProxyType.FTP));

    // socks
    panel.addPropertyType(ProxyType.SOCKS + ".host", PropertyType.STRING);
    panel.setLabel(ProxyType.SOCKS + ".host", "Socks - host");
    panel.setHelp(ProxyType.SOCKS + ".host", "The URL of the proxy");
    props.setProperty(ProxyType.SOCKS + ".host", ProxyUtils.getProxyHost(ProxyType.SOCKS));
    panel.addPropertyType(ProxyType.SOCKS + ".port", PropertyType.INTEGER);
    panel.setLabel(ProxyType.SOCKS + ".port", "Socks - port");
    panel.setHelp(ProxyType.SOCKS + ".port", "The port of the proxy (0-65535)");
    props.setProperty(ProxyType.SOCKS + ".port", "" + ProxyUtils.getProxyPort(ProxyType.SOCKS));

    panel.setPropertyOrder(new String[]{
      ProxyType.HTTP + ".host",
      ProxyType.HTTP + ".port",
      ProxyType.FTP + ".host",
      ProxyType.FTP + ".port",
      ProxyType.SOCKS + ".host",
      ProxyType.SOCKS + ".port",
    });
    panel.setProperties(props);

    if (GUIHelper.getParentDialog(this) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(this), true);
    dialog.setTitle("Proxy settings");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setSize((int) (dialog.getWidth() * 1.5), dialog.getHeight());
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    props = panel.getProperties();
    setProxy(ProxyType.HTTP, props);
    setProxy(ProxyType.FTP, props);
    setProxy(ProxyType.SOCKS, props);
  }

  /**
   * For updating the available Weka versions.
   */
  public void updateVersions() {
    SwingWorker	worker;

    worker = new SwingWorker() {
      protected String msg;
      @Override
      protected Object doInBackground() throws Exception {
	msg = Versions.update(true, new DefaultCapture());
	return null;
      }
      @Override
      protected void done() {
	super.done();
	if (msg != null)
	  GUIHelper.showErrorMessage(getParent(), "Failed to update:\n" + msg);
	else
	  JOptionPane.showMessageDialog(getParent(), "Successfully updated available Weka versions!");
      }
    };
    worker.execute();
  }

  /**
   * For downloading and installation Weka versions.
   */
  public void downloadVersion() {
    ApprovalDialog		dialog;
    PropertiesParameterPanel	panel;
    Properties			props;
    List<String>		versions;
    final String		installVersion;
    final String		installDir;
    final Install 		cmd;
    final CommandOutputPanel	outputPanel;
    SwingWorker			worker;

    if (!Versions.isVersionsFilePresent()) {
      GUIHelper.showErrorMessage(getParent(), "Please update the available Weka versions first ('Update' menu item)!");
      return;
    }
    try {
      versions = Versions.getAvailableVersions();
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(getParent(), "Failed to load available Weka versions!", e);
      return;
    }

    panel = new PropertiesParameterPanel();
    props = new Properties();

    panel.addPropertyType("version", PropertyType.COMMA_SEPARATED_LIST_FIXED);
    panel.setLabel("version", "Weka version");
    panel.setHelp("version", "The version of Weka to install");
    props.setProperty("version", Utils.flatten(versions, ","));

    panel.addPropertyType("dir", PropertyType.DIRECTORY_ABSOLUTE);
    panel.setLabel("dir", "Installation directory");
    panel.setHelp("dir", "The directory to install Weka in; a sub-directory with the version number will get created below this directory");
    props.setProperty("dir", System.getProperty("user.home"));

    panel.setPropertyOrder(new String[]{"version", "dir"});
    panel.setProperties(props);

    if (GUIHelper.getParentDialog(this) != null)
      dialog = new ApprovalDialog(GUIHelper.getParentDialog(this), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new ApprovalDialog(GUIHelper.getParentFrame(this), true);
    dialog.setTitle("Install Weka");
    dialog.getContentPane().add(panel, BorderLayout.CENTER);
    dialog.pack();
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION)
      return;

    props          = panel.getProperties();
    installVersion = props.getProperty("version");
    installDir     = props.getProperty("dir");
    cmd            = new Install();
    outputPanel    = new CommandOutputPanel();
    outputPanel.setOwner(cmd);
    outputPanel.setTabbedPane(getTabbedPane());
    SwingUtilities.invokeLater(() -> getTabbedPane().addTab("Installing " + installVersion, outputPanel));
    worker = new SwingWorker() {
      protected List<String> errors;
      @Override
      protected Object doInBackground() throws Exception {
        errors = new ArrayList<>();
	try {
	  boolean success = cmd.execute(new String[]{
	    "--action",
	    "download",
	    "--version",
	    installVersion,
	    "--install-dir",
	    installDir,
	  });
	  if (!success) {
	    if (cmd.hasErrors())
	      errors.add(cmd.getErrors());
	    else
	      errors.add("Failed to install Weka " + installVersion + "!");
	  }
	}
	catch (Exception e) {
	  errors.add("Failed to install Weka " + installVersion + ": " + e);
	}
	return null;
      }
      @Override
      protected void done() {
	super.done();
	if (!errors.isEmpty())
	  GUIHelper.showErrorMessage(getTabbedPane(), Utils.flatten(errors, "\n"));
	else
	  JOptionPane.showMessageDialog(getParent(), "Successfully installed Weka " + installVersion + "!");
      }
    };
    worker.execute();
  }
}
