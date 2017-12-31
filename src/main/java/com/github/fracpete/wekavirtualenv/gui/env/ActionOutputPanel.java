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
 * Copyright (C) 2017-2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import com.github.fracpete.jclipboardhelper.ClipboardHelper;
import com.github.fracpete.wekavirtualenv.command.OutputListener;
import com.github.fracpete.wekavirtualenv.gui.core.FileChooser;
import com.github.fracpete.wekavirtualenv.gui.core.IconHelper;
import nz.ac.waikato.cms.core.FileUtils;
import nz.ac.waikato.cms.gui.core.BasePanel;
import nz.ac.waikato.cms.gui.core.BaseScrollPane;
import nz.ac.waikato.cms.gui.core.ExtensionFileFilter;
import nz.ac.waikato.cms.gui.core.GUIHelper;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
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

  /** the buttons on the left. */
  protected JPanel m_PanelButtonsLeft;

  /** the buttons on the right. */
  protected JPanel m_PanelButtonsRight;

  /** the button for clearing the output. */
  protected JButton m_ButtonClear;

  /** the button for copying the output. */
  protected JButton m_ButtonCopy;

  /** the button for saving the output. */
  protected JButton m_ButtonSave;

  /** the button for stopping the process. */
  protected JButton m_ButtonStop;

  /** the button for closing the output. */
  protected JButton m_ButtonClose;

  /** the file chooser. */
  protected FileChooser m_FileChooser;

  /**
   * Initializes the widgets;
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());
    m_TextArea = new JTextArea();
    m_TextArea.setFont(new Font("monospaced", Font.PLAIN, 12));
    m_TextArea.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	updateButtons();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	updateButtons();
      }
    });
    add(new BaseScrollPane(m_TextArea));

    // buttons
    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.SOUTH);
    m_PanelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(m_PanelButtonsLeft, BorderLayout.WEST);
    m_PanelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panel.add(m_PanelButtonsRight, BorderLayout.EAST);

    m_ButtonClear = new JButton(IconHelper.getIcon("Clear"));
    m_ButtonClear.setToolTipText("Clears the output");
    m_ButtonClear.addActionListener((ActionEvent e) -> clear());
    m_PanelButtonsLeft.add(m_ButtonClear);

    m_ButtonCopy = new JButton(IconHelper.getIcon("Copy"));
    m_ButtonCopy.setToolTipText("Copies the output to the clipboard");
    m_ButtonCopy.addActionListener((ActionEvent e) -> copy());
    m_PanelButtonsLeft.add(m_ButtonCopy);

    m_ButtonSave = new JButton(IconHelper.getIcon("Save"));
    m_ButtonSave.setToolTipText("Saves the output to a file");
    m_ButtonSave.addActionListener((ActionEvent e) -> save());
    m_PanelButtonsLeft.add(m_ButtonSave);

    m_ButtonStop = new JButton(IconHelper.getIcon("Stop"));
    m_ButtonStop.setToolTipText("Stops the process");
    m_ButtonStop.addActionListener((ActionEvent e) -> stop());
    m_PanelButtonsRight.add(m_ButtonStop);

    m_ButtonClose = new JButton(IconHelper.getIcon("Close"));
    m_ButtonClose.setToolTipText("Stops the process (if still running) and closes the tab");
    m_ButtonClose.addActionListener((ActionEvent e) -> close());
    m_PanelButtonsRight.add(m_ButtonClose);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
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
    boolean 	atEnd;

    atEnd = (m_TextArea.getCaretPosition() == m_TextArea.getText().length());
    m_TextArea.append((stdout ? "[OUT] " : "[ERR] ") + line + "\n");
    if (atEnd)
      m_TextArea.setCaretPosition(m_TextArea.getText().length());
    updateButtons();
  }

  /**
   * Clears the output.
   */
  public void clear() {
    m_TextArea.setText("");
    updateButtons();
  }

  /**
   * Copies the output to the clipboard.
   */
  public void copy() {
    if (m_TextArea.getSelectedText() == null)
      ClipboardHelper.copyToClipboard(m_TextArea.getText());
    else
      ClipboardHelper.copyToClipboard(m_TextArea.getSelectedText());
  }

  /**
   * Saves the output.
   */
  public void save() {
    int		retVal;
    String	msg;

    if (m_FileChooser == null) {
      m_FileChooser = new FileChooser();
      m_FileChooser.addChoosableFileFilter(new ExtensionFileFilter("Text file", "txt"));
      m_FileChooser.setAcceptAllFileFilterUsed(true);
    }

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != FileChooser.APPROVE_OPTION)
      return;

    msg = FileUtils.writeToFileMsg(m_FileChooser.getSelectedFile().getAbsolutePath(), m_TextArea.getText(), false, null);
    if (msg != null)
      GUIHelper.showErrorMessage(this, msg);
  }

  /**
   * Stops the process.
   */
  public void stop() {
    m_Action.getOwner().destroy();
  }

  /**
   * Removes itself from the tabbed pane.
   */
  public void close() {
    stop();
    m_TabbedPane.remove(this);
  }

  /**
   * Updates the enabled state of the buttons.
   */
  protected void updateButtons() {
    boolean	hasText;

    hasText = (m_TextArea.getText().length() > 0);
    m_ButtonClear.setEnabled(hasText);
    m_ButtonCopy.setEnabled(hasText);
    m_ButtonSave.setEnabled(hasText);
  }
}
