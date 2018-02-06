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
 * OutputListenerSupporter.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

/**
 * Interface for classes that support output listening.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface OutputListenerSupporter {

  /**
   * Adds the output listener.
   *
   * @param l		the listener
   */
  public void addOutputListener(OutputListener l);

  /**
   * Removes the output listener.
   *
   * @param l		the listener
   */
  public void removeOutputListener(OutputListener l);
}
