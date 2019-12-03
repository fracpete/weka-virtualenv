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
 * Install.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.requests4j.Requests;
import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.core.FileDownload;
import com.github.fracpete.wekavirtualenv.core.ProxyUtils;
import com.github.fracpete.wekavirtualenv.core.Versions;
import com.github.fracpete.wekavirtualenv.core.ZipUtils;
import nz.ac.waikato.cms.core.Utils;

import java.io.File;
import java.util.List;

/**
 * Downloads and installs a specific Weka version.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Install
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "install";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "Downloads and installs a specific Weka version.\n"
      + "NB: The downloaded zip file contains a sub-directory with the version of Weka.";
  }

  /**
   * Returns the parser to use for the arguments.
   *
   * @return		always null
   */
  @Override
  public ArgumentParser getParser() {
    ArgumentParser 	result;

    result = new ArgumentParser(getName());
    result.addOption("--action")
      .dest("action")
      .help("the action to perform: update|list|download")
      .required(true);
    result.addOption("--version")
      .dest("version")
      .help("the version to install (for action 'download')")
      .setDefault("");
    result.addOption("--install-dir")
      .dest("install-dir")
      .help("the directory to install Weka in (for action 'download')")
      .setDefault("");

    return result;
  }

  /**
   * Updates the versions file.
   *
   * @return		true if successful
   */
  protected boolean update() {
    String	msg;

    msg = Versions.update(this);
    if (msg != null)
      addError(msg);

    return (msg == null);
  }

  /**
   * Lists the available versions.
   *
   * @return		true if successful
   */
  protected boolean list() {
    boolean		result;
    List<String>	list;
    StringBuilder	versions;
    int			i;

    result = true;
    versions = new StringBuilder();
    try {
      list = Versions.getAvailableVersions();
      for (i = 0; i < list.size(); i++) {
        if (i > 0)
          versions.append("\n");
        versions.append(list.get(i));
      }
      println("Available versions:\n" + versions, true);
    }
    catch (Exception e) {
      addError("Failed to list versions!\n" + Utils.throwableToString(e));
      result = false;
    }

    return result;
  }

  /**
   * Downloads the specified Weka version.
   *
   * @param version	the version to install
   * @param installDir 	the installation directory
   * @return		true if successful
   */
  protected boolean download(String version, String installDir) {
    boolean		result;
    String		url;
    String 		tmpZip;
    StringBuilder	errors;
    FileDownload	response;

    result = true;
    try {
      // download file
      url    = Versions.getURL(version);
      tmpZip = System.getProperty("java.io.tmpdir") + File.separator + version + ".zip";
      response = ProxyUtils.applyProxy(Requests.get(url)).allowRedirects(true).execute(new FileDownload(tmpZip, this));
      if (!response.ok()) {
        addError("Failed to download '" + url + "': " + response.statusCode() + "/" + response.statusMessage());
        return false;
      }

      // unzip
      println("Unzipping " + tmpZip + " to " + installDir + "...", true);
      errors = new StringBuilder();
      ZipUtils.decompress(new File(tmpZip), new File(installDir), true, errors);
      if (errors.length() > 0) {
	addError(errors.toString());
	return false;
      }
    }
    catch (Exception e) {
      addError("Failed to download version: " + version + "\n" + Utils.throwableToString(e));
      result = false;
    }

    if (result)
      println("Successfully installed " + version + " to " + installDir, true);
    else
      println("Failed to install " + version, true);

    return result;
  }

  /**
   * Executes the command.
   *
   * @param ns		the namespace of the parsed options, null if no options to parse
   * @param options	additional command-line options
   * @return		true if successful
   */
  @Override
  protected boolean doExecute(Namespace ns, String[] options) {
    boolean	result;

    result = true;

    switch (ns.getString("action")) {
      case "update":
        result = update();
        break;

      case "list":
        if (!Versions.isVersionsFilePresent())
          result = update();
        if (result)
	  result = list();
        break;

      case "download":
        if (ns.getString("version").isEmpty()) {
          addError("No Weka version specified!");
          return false;
	}
        if (ns.getString("install-dir").isEmpty()) {
          addError("No installation directory specified!");
          return false;
	}
        if (!Versions.isVersionsFilePresent())
          result = update();
        if (result)
	  result = download(ns.getString("version"), ns.getString("install-dir"));
        break;

      default:
        addError("Unknown action: '" + ns.getString("action") + "'");
        return false;
    }

    return result;
  }
}
