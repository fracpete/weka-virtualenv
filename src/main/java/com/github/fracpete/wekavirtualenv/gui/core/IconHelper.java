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
 * IconHelper.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.core;

import javax.swing.ImageIcon;
import java.net.URL;

/**
 * Helper class for icons.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class IconHelper {

  /** the image directory. */
  public final static String IMAGE_DIR = "com/github/fracpete/wekavirtualenv/gui/images/";

  /**
   * Loads and returns the icon with the specified filename.
   *
   * @param filename	the name of the icon, (no extension, .png by default)
   * @return		the icon, null if failed to load
   */
  public static ImageIcon getIcon(String filename) {
    URL		resource;

    resource = ClassLoader.getSystemClassLoader().getResource(filename + ".png");
    if (resource != null)
      return new ImageIcon(resource);
    else
      return null;
  }
}
