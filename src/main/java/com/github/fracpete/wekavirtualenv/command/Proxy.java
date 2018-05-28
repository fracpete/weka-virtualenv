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
 * Proxy.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.command;

import com.github.fracpete.inetutils4j.api.Proxy.ProxyType;
import com.github.fracpete.simpleargparse4j.ArgumentParser;
import com.github.fracpete.simpleargparse4j.Namespace;
import com.github.fracpete.wekavirtualenv.core.ProxyUtils;
import nz.ac.waikato.cms.core.Utils;

/**
 * For managing proxy settings.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Proxy
  extends AbstractCommand {

  /**
   * The name of the command (used on the commandline).
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "proxy";
  }

  /**
   * Returns a short help string.
   *
   * @return		the help string
   */
  public String getHelp() {
    return "For managing proxy settings: listing, setting, removing.";
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
      .help("the action to perform: show|set|unset")
      .required(true);
    result.addOption("--type")
      .dest("type")
      .help("the proxy type (" + Utils.flatten(ProxyType.values(), "|") + "),\nused by actions 'set' and 'unset'")
      .setDefault("");
    result.addOption("--host")
      .dest("host")
      .help("the proxy host, used by action 'set'")
      .setDefault("");
    result.addOption("--port")
      .dest("port")
      .help("the proxy port, used by action 'set'")
      .setDefault(-1);

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
    String	host;
    int		port;
    ProxyType	type;

    result = true;

    switch (ns.getString("action")) {
      case "show":
        for (ProxyType t: ProxyType.values()) {
          host = ProxyUtils.getProxyHost(t);
          port = ProxyUtils.getProxyPort(t);
          if (!host.isEmpty() && (port > -1)) {
	    println("" + t + ":", true);
	    println("- host: " + host, true);
	    println("- port: " + port, true);
	  }
	  else {
	    println("" + t + ": not set", true);
	  }
	}
        break;

      case "set":
        if (ns.getString("type").isEmpty()) {
          addError("No proxy type specified!");
          return false;
	}
	type = ProxyUtils.strToType(ns.getString("type"));
	if (type == null) {
          addError("Invalid proxy type: " + ns.getString("type"));
          return false;
	}
        if (ns.getInt("port") == -1) {
          addError("No proxy port specified!");
          return false;
	}
	host = ns.getString("host");
	port = ns.getInt("port");
	ProxyUtils.setProxy(type, host, port);
	System.out.println("Set proxy for " + type + ": " + host + ", port " + port);
        break;

      case "unset":
        if (ns.getString("type").isEmpty()) {
          addError("No proxy type specified!");
          return false;
	}
	type = ProxyUtils.strToType(ns.getString("type"));
	if (type == null) {
          addError("Invalid proxy type: " + ns.getString("type"));
          return false;
	}
	ProxyUtils.unsetProxy(type);
	System.out.println("Removed proxy for " + type);
        break;

      default:
        addError("Unknown action: '" + ns.getString("action") + "'");
        return false;
    }

    return result;
  }
}
