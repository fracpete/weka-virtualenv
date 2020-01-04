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
 * EnvironmentPanelCellRenderer.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.gui.env;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Component;

/**
 * Renderer for environment panels.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class EnvironmentPanelCellRenderer
  implements ListCellRenderer<EnvironmentPanel> {

  /**
   * Configures/returns the renderer for the panel.
   *
   * @param list	the list this is for
   * @param value	the element to render
   * @param index	the index in the list
   * @param isSelected	whether the element is selected
   * @param cellHasFocus	whether the element is focused
   * @return
   */
  @Override
  public Component getListCellRendererComponent(JList<? extends EnvironmentPanel> list, EnvironmentPanel value, int index, boolean isSelected, boolean cellHasFocus) {
    if (isSelected) {
      if (cellHasFocus)
	value.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY.darker(), Color.DARK_GRAY.darker()));
      else
	value.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY, Color.DARK_GRAY));
    }
    else {
      value.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.LIGHT_GRAY.brighter(), Color.DARK_GRAY.brighter()));
    }
    return value;
  }
}
