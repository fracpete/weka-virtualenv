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
 * FileChooser.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.core;

import nz.ac.waikato.cms.gui.core.DirectoryBookmarks.FileChooserBookmarksPanel;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import java.awt.Dimension;
import java.io.File;

/**
 * File chooser with support for bookmarks.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileChooser
  extends JFileChooser {

  /** the bookmarks. */
  protected FileChooserBookmarksPanel m_PanelBookmarks;

  /**
   * Constructs a <code>FileChooser</code> pointing to the user's
   * default directory. This default depends on the operating system.
   * It is typically the "My Documents" folder on Windows, and the
   * user's home directory on Unix.
   */
  public FileChooser() {
    super();

    initialize();
  }

  /**
   * Constructs a <code>FileChooser</code> using the given path.
   * Passing in a <code>null</code>
   * string causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectoryPath  a <code>String</code> giving the path
   *				to a file or directory
   */
  public FileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);

    initialize();
  }

  /**
   * Constructs a <code>FileChooser</code> using the given <code>File</code>
   * as the path. Passing in a <code>null</code> file
   * causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectory  a <code>File</code> object specifying
   *				the path to a file or directory
   */
  public FileChooser(File currentDirectory) {
    super(currentDirectory);

    initialize();
  }

  /**
   * For initializing some stuff.
   */
  protected void initialize() {
    JComponent accessory;

    accessory = createAccessoryPanel();
    if (accessory != null)
      setAccessory(accessory);
    setPreferredSize(new Dimension(750, 500));
  }

  /**
   * Creates an accessory panel displayed next to the files.
   *
   * @return		the panel or null if none available
   */
  protected JComponent createAccessoryPanel() {
    m_PanelBookmarks = new FileChooserBookmarksPanel();
    m_PanelBookmarks.setOwner(this);
    m_PanelBookmarks.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 0));
    return m_PanelBookmarks;
  }
}
