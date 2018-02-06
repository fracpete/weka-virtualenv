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
 * Script.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.command;

import com.github.fracpete.wekavirtualenv.command.OutputListener;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import com.github.fracpete.wekavirtualenv.gui.core.Stoppable;
import nz.ac.waikato.cms.core.FileUtils;
import nz.ac.waikato.cms.core.Utils;
import nz.ac.waikato.cms.gui.core.BaseFileChooser;
import nz.ac.waikato.cms.gui.core.BasePanel;
import nz.ac.waikato.cms.gui.core.BaseTextPaneWithWordWrap;
import nz.ac.waikato.cms.gui.core.ExtensionFileFilter;
import nz.ac.waikato.cms.gui.core.GUIHelper;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

/**
 * Executes a class with parmaeters.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Script
  extends AbstractGUICommand {

  /**
   * The panel for writing and executing scripts.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   */
  public static class ScriptPanel
    extends BasePanel
    implements OutputListener, Stoppable {

    /** the owner. */
    protected JTabbedPane m_Owner;

    /** the text area for the script. */
    protected BaseTextPaneWithWordWrap m_TextScript;

    /** the text area for the script output. */
    protected BaseTextPaneWithWordWrap m_TextOutput;

    /** the split pane. */
    protected JSplitPane m_SplitPane;

    /** the button for closing the tab. */
    protected JButton m_ButtonClose;

    /** the button for loading a script. */
    protected JButton m_ButtonLoadScript;

    /** the button for saving a script. */
    protected JButton m_ButtonSaveScript;

    /** the button for running a script. */
    protected JButton m_ButtonRunScript;

    /** the button for stopping a script. */
    protected JButton m_ButtonStopScript;

    /** the button for saving the output. */
    protected JButton m_ButtonSaveOutput;

    /** the current filename. */
    protected File m_CurrentFile;

    /** the filechooser for scripts. */
    protected BaseFileChooser m_FileChooser;

    /** the command. */
    protected com.github.fracpete.wekavirtualenv.command.Script m_Command;

    /** the attribute set for stdout. */
    protected SimpleAttributeSet m_StdOutAttributeSet;

    /** the attribute set for stderr. */
    protected SimpleAttributeSet m_StdErrAttributeSet;

    /** the counter for temp files. */
    protected static int m_Counter;
    static {
      m_Counter = 0;
    }

    /**
     * Initializes the panel.
     *
     * @param owner	the tabbed pane the panel belongs to
     */
    public ScriptPanel(JTabbedPane owner) {
      super();
      m_Owner = owner;
    }

    /**
     * Initializes the members.
     */
    @Override
    protected void initialize() {
      super.initialize();

      m_CurrentFile = null;
      m_FileChooser = new BaseFileChooser();
      m_FileChooser.addChoosableFileFilter(new ExtensionFileFilter("Script", "scr"));
      m_FileChooser.setAcceptAllFileFilterUsed(true);

      m_StdOutAttributeSet = new SimpleAttributeSet();
      StyleConstants.setForeground(m_StdOutAttributeSet, Color.BLACK);
      StyleConstants.setFontFamily(m_StdOutAttributeSet, "monospaced");

      m_StdErrAttributeSet = new SimpleAttributeSet();
      StyleConstants.setForeground(m_StdErrAttributeSet, Color.RED.darker());
      StyleConstants.setFontFamily(m_StdErrAttributeSet, "monospaced");
    }

    /**
     * Initializes the widgets.
     */
    @Override
    protected void initGUI() {
      JPanel	panelText;
      JPanel	panelButtons;

      super.initGUI();

      setLayout(new BorderLayout());

      panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      add(panelButtons, BorderLayout.SOUTH);
      m_ButtonClose = new JButton(IconHelper.getIcon("Close"));
      m_ButtonClose.addActionListener((ActionEvent e) -> close());
      panelButtons.add(m_ButtonClose);

      m_SplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      m_SplitPane.setResizeWeight(0.5);
      m_SplitPane.setDividerLocation(300);
      m_SplitPane.setOneTouchExpandable(true);
      add(m_SplitPane, BorderLayout.CENTER);

      panelText = new JPanel(new BorderLayout());
      m_SplitPane.setTopComponent(panelText);
      m_TextScript = new BaseTextPaneWithWordWrap();
      m_TextScript.setWordWrap(false);
      m_TextScript.getTextPane().setFont(Font.decode("monospaced"));
      panelText.add(m_TextScript, BorderLayout.CENTER);
      panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panelText.add(panelButtons, BorderLayout.SOUTH);
      m_ButtonLoadScript = new JButton(IconHelper.getIcon("Open"));
      m_ButtonLoadScript.addActionListener((ActionEvent e) -> loadScript());
      panelButtons.add(m_ButtonLoadScript);
      m_ButtonSaveScript = new JButton(IconHelper.getIcon("Save"));
      m_ButtonSaveScript.addActionListener((ActionEvent e) -> saveScript());
      panelButtons.add(m_ButtonSaveScript);
      m_ButtonRunScript = new JButton(IconHelper.getIcon("Run"));
      m_ButtonRunScript.addActionListener((ActionEvent e) -> execute());
      panelButtons.add(m_ButtonRunScript);
      m_ButtonStopScript = new JButton(IconHelper.getIcon("Stop"));
      m_ButtonStopScript.addActionListener((ActionEvent e) -> stop());
      panelButtons.add(m_ButtonStopScript);

      panelText = new JPanel(new BorderLayout());
      m_SplitPane.setBottomComponent(panelText);
      m_TextOutput = new BaseTextPaneWithWordWrap();
      m_TextOutput.setWordWrap(false);
      m_TextOutput.getTextPane().setFont(Font.decode("monospaced"));
      panelText.add(m_TextOutput, BorderLayout.CENTER);
      panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panelText.add(panelButtons, BorderLayout.SOUTH);
      m_ButtonSaveOutput = new JButton(IconHelper.getIcon("Save"));
      m_ButtonSaveOutput.addActionListener((ActionEvent e) -> saveScript());
      panelButtons.add(m_ButtonSaveOutput);
    }

    /**
     * Loads the specified script file.
     *
     * @param file	the file to load
     */
    protected void loadScript(File file) {
      List<String>	lines;

      try {
        lines = Files.readAllLines(file.toPath());
        m_TextScript.setText(Utils.flatten(lines, "\n"));
	m_CurrentFile = file;
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(this, "Failed to load script: " + file, e);
      }
    }

    /**
     * Lets the user load a script.
     */
    public void loadScript() {
      int	retVal;

      retVal = m_FileChooser.showOpenDialog(this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
        return;

      loadScript(m_FileChooser.getSelectedFile());
    }

    /**
     * Lets the user save the current script.
     */
    public void saveScript() {
      if (m_CurrentFile == null) {
        saveScriptAs();
        return;
      }
      saveScript(m_CurrentFile);
    }

    /**
     * Saves the current script under the specified file name.
     *
     * @param file	the file to save to
     * @see		#m_CurrentFile
     */
    protected void saveScript(File file) {
      String	msg;

      msg = FileUtils.writeToFileMsg(file.getAbsolutePath(), m_TextScript.getText(), false, null);
      if (msg != null)
        GUIHelper.showErrorMessage(this, "Failed to save script to: " + file + "\n" + msg);
    }

    /**
     * Lets the user save the current script under a new name.
     */
    public void saveScriptAs() {
      int	retVal;
      File	file;

      retVal = m_FileChooser.showSaveDialog(this);
      if (retVal != BaseFileChooser.APPROVE_OPTION)
        return;

      file = m_FileChooser.getSelectedFile();
      saveScript(file);
      m_CurrentFile = file;
    }

    /**
     * Saves the current script to a temp file and returns the name.
     *
     * @return		the generated file name
     */
    protected File saveScriptToTempFile() {
      File	result;

      result = new File(
        System.getProperty("java.io.tmpdir")
	  + File.separator
	  + Long.toHexString(System.currentTimeMillis())
	  + "-"
	  + Integer.toHexString(m_Counter++)
	  + ".scr");
      saveScript(result);

      return result;
    }

    /**
     * Gets called when output was produced.
     *
     * @param line	the line to process
     * @param stdout	whether stdout or stderr
     */
    public void outputOccurred(String line, boolean stdout) {
      boolean 	atEnd;

      atEnd = (m_TextOutput.getCaretPosition() == m_TextOutput.getText().length());
      m_TextOutput.append(line + "\n", stdout ? m_StdOutAttributeSet : m_StdErrAttributeSet);
      if (atEnd)
	m_TextOutput.setCaretPosition(m_TextOutput.getText().length());
    }

    /**
     * Executes the currently loaded script.
     */
    public void execute() {
      SwingWorker	worker;
      final ScriptPanel	owner;

      owner  = this;
      worker = new SwingWorker() {
	protected String m_Error;
	@Override
	protected Object doInBackground() throws Exception {
	  m_TextOutput.setText("");
	  File tmpFile = saveScriptToTempFile();
	  m_Command = new com.github.fracpete.wekavirtualenv.command.Script();
	  m_Command.addOutputListener(owner);
	  boolean result = m_Command.execute(new String[]{
	    "--file",
	    tmpFile.getAbsolutePath(),
	  });
	  if (!result) {
	    if (m_Command.hasErrors())
	      m_Error = m_Command.getErrors();
	    else
	      m_Error = "Failed to execute script!";
	  }
	  return null;
	}
	@Override
	protected void done() {
	  super.done();
	  m_Command = null;
	  if (m_Error != null)
	    GUIHelper.showErrorMessage(owner, m_Error);
	}
      };
      worker.execute();
    }

    /**
     * Destroys the process if possible.
     */
    public void stop() {
      if (m_Command != null)
	m_Command.destroy();
    }

    /**
     * Removes itself from the tabbed pane (after stopping).
     */
    public void close() {
      stop();
      m_Owner.remove(this);
    }
  }

  /** the script panel. */
  protected ScriptPanel m_PanelScript;

  /** the counter for the script panels. */
  protected static int m_PanelCounter;
  static {
    m_PanelCounter = 0;
  }

  /**
   * Returns the name of the action (displayed in GUI).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Script";
  }

  /**
   * The group this action belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "run";
  }

  /**
   * Returns whether the action requires an environment.
   *
   * @return		true if the action requires an environment
   */
  @Override
  public boolean requiresEnvironment() {
    return false;
  }

  /**
   * Returns whether the action generates console output.
   *
   * @return		true if the action generates console output
   */
  public boolean generatesOutput() {
    return false;
  }

  /**
   * Performs the actual execution.
   *
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doExecute() {
    m_PanelCounter++;
    m_PanelScript = new ScriptPanel(getTabbedPane());
    getTabbedPane().addTab("Script (" + m_PanelCounter + ")", m_PanelScript);
    return null;
  }

  /**
   * Destroys the process if possible.
   */
  public void destroy() {
    if (m_PanelScript != null)
      m_PanelScript.stop();
  }
}
