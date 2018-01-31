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
 * ArffCommandSelector.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui;

import com.github.fracpete.wekavirtualenv.command.AbstractCommand;
import com.github.fracpete.wekavirtualenv.command.Command;
import com.github.fracpete.wekavirtualenv.command.DatasetHandler;
import com.github.fracpete.wekavirtualenv.env.Environment;
import com.github.fracpete.wekavirtualenv.env.Environments;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import nz.ac.waikato.cms.gui.core.BaseFrame;
import nz.ac.waikato.cms.gui.core.BasePanel;
import nz.ac.waikato.cms.gui.core.GUIHelper;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel;
import nz.ac.waikato.cms.gui.core.PropertiesParameterPanel.PropertyType;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;
import nz.ac.waikato.cms.locator.ClassLocator;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Lets the user select the environment and user interface to handle
 * the provided datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ArffCommandSelector
  extends BasePanel {

  /** the panel with the comboboxes. */
  protected PropertiesParameterPanel m_PanelParams;

  /** the button to launch the command. */
  protected JButton m_ButtonLaunch;

  /** the button to close the dialog. */
  protected JButton m_ButtonClose;

  /** the datasets. */
  protected String[] m_Datasets;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Datasets = new String[0];
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel		panel;
    Properties		props;
    List<String>	list;

    super.initGUI();

    setLayout(new BorderLayout());

    // combox boxes
    m_PanelParams = new PropertiesParameterPanel();
    add(m_PanelParams, BorderLayout.CENTER);
    props = new Properties();

    m_PanelParams.addPropertyType("env", PropertyType.BLANK_SEPARATED_LIST_FIXED);
    m_PanelParams.setLabel("env", "Environment");
    list = new ArrayList<>();
    for (Environment env: Environments.list())
      list.add(env.name);
    props.setProperty("env", OptionUtils.joinOptions(list.toArray(new String[list.size()])));

    m_PanelParams.addPropertyType("cmd", PropertyType.BLANK_SEPARATED_LIST_FIXED);
    m_PanelParams.setLabel("cmd", "Command");
    list = new ArrayList<>();
    for (AbstractCommand cmd: getCommands())
      list.add(cmd.getName());
    props.setProperty("cmd", OptionUtils.joinOptions(list.toArray(new String[list.size()])));

    m_PanelParams.setPropertyOrder(new String[]{
      "env",
      "cmd"
    });

    m_PanelParams.setProperties(props);

    // buttons
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    add(panel, BorderLayout.SOUTH);

    m_ButtonLaunch = new JButton("Launch");
    m_ButtonLaunch.addActionListener((ActionEvent e) -> launch());
    panel.add(m_ButtonLaunch);

    m_ButtonClose = new JButton("Close", IconHelper.getIcon("Close"));
    m_ButtonClose.addActionListener((ActionEvent e) -> close());
    panel.add(m_ButtonClose);
  }

  /**
   * Lists all dataset handling commands.
   *
   * @return		the commands
   */
  protected List<AbstractCommand> getCommands() {
    List<AbstractCommand>	result;
    List<Class> 		classes;
    AbstractCommand		cmd;

    result = new ArrayList<>();
    classes = ClassLocator.getSingleton().findClasses(
      DatasetHandler.class,
      new String[]{DatasetHandler.class.getPackage().getName()});

    for (Class cls: classes) {
      try {
	cmd = (AbstractCommand) cls.newInstance();
	result.add(cmd);
      }
      catch (Exception e) {
	// ignored
      }
    }

    Collections.sort(result);

    return result;
  }

  /**
   * Interprets the arguments.
   *
   * @param args	the command-line arguments, ie files
   */
  public void execute(String[] args) {
    if (args.length == 0) {
      JOptionPane.showMessageDialog(this, "No file names provided!", "Error", JOptionPane.ERROR_MESSAGE);
      close();
    }
    m_Datasets = args;
  }

  /**
   * Launches the selected
   */
  public void launch() {
    final Command 	cmd;
    Properties		props;
    SwingWorker		worker;

    props = m_PanelParams.getProperties();

    if (props.getProperty("env").isEmpty()) {
      JOptionPane.showMessageDialog(this, "No environment selected!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    cmd = AbstractCommand.getCommand(props.getProperty("cmd"));
    if (cmd == null) {
      JOptionPane.showMessageDialog(this, "No command selected!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (cmd.requiresEnvironment())
      cmd.setEnv(Environments.readEnv(props.getProperty("env")));

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	cmd.execute(m_Datasets);
	return null;
      }
    };
    worker.execute();
  }

  /**
   * For closing the frame/dialog.
   */
  public void close() {
    GUIHelper.closeParent(this);
  }

  /**
   * Displays the GUI.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    final ArffCommandSelector panel = new ArffCommandSelector();
    BaseFrame frame = new BaseFrame("Arff Command Selector");
    frame.setIconImage(IconHelper.getIcon("wenv").getImage());
    frame.setDefaultCloseOperation(BaseFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        panel.close();
      }
    });
    frame.setVisible(true);
    panel.execute(args);
  }
}
