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
 * Copyright (C) 2018-2022 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.core;

import com.github.fracpete.requests4j.request.Request;
import nz.ac.waikato.cms.core.PropsUtils;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Properties;

/**
 * Helper class for Proxy support.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ProxyUtils {

  /** the proxy file name. */
  public final static String PROXY_NAME = "proxy.props";

  public enum ProxyType {
    HTTP,
    FTP
  }

  /** the properties. */
  protected static Properties m_Properties;

  /** whether the settings need updating. */
  protected static boolean m_Invalid = true;

  /**
   * Returns the properties.
   *
   * @return		the properties
   */
  protected static synchronized Properties getProperties() {
    if (m_Properties == null) {
      if (isProxyFilePresent()) {
        try {
          m_Properties = new Properties();
	  PropsUtils.load(m_Properties, getProxyFile());
	}
	catch (Exception e) {
          m_Properties = new Properties();
	}
      }
      else {
        m_Properties = new Properties();
      }
    }
    return m_Properties;
  }

  /**
   * Saves the current properties.
   *
   * @return		true if successfully saved
   */
  protected static synchronized boolean saveProperties() {
    if (m_Properties == null)
      m_Properties = new Properties();
    Project.createHomeDir();
    return PropsUtils.save(m_Properties, getProxyFile());
  }

  /**
   * Returns the location of the proxy file.
   *
   * @return		the file
   */
  public static String getProxyFile() {
    return Project.getHomeDir() + File.separator + PROXY_NAME;
  }

  /**
   * Checks whether the proxy file exists.
   *
   * @return		true if it exists
   */
  public static boolean isProxyFilePresent() {
    return new File(getProxyFile()).exists();
  }

  /**
   * Sets the proxy in the props file.
   *
   * @param type	the type of proxy
   * @param host	the host
   * @param port	the port
   */
  public static void setProxy(ProxyType type, String host, int port) {
    Properties	props;

    m_Invalid = true;
    props     = getProperties();
    switch (type) {
      case HTTP:
	props.setProperty("http.proxyHost", host);
	props.setProperty("http.proxyPort", "" + port);
	break;
      case FTP:
	props.setProperty("ftp.proxyHost", host);
	props.setProperty("ftp.proxyPort", "" + port);
	break;
      default:
        throw new IllegalStateException("Unhandled proxy type: " + type);
    }
    saveProperties();
  }

  /**
   * Removes the proxy from the props file.
   *
   * @param type	the type of proxy to reove
   */
  public static void unsetProxy(ProxyType type) {
    Properties	props;

    m_Invalid = true;
    props     = getProperties();
    switch (type) {
      case HTTP:
	props.remove("http.proxyHost");
	props.remove("http.proxyPort");
	break;
      case FTP:
	props.remove("ftp.proxyHost");
	props.remove("ftp.proxyPort");
	break;
      default:
        throw new IllegalStateException("Unhandled proxy type: " + type);
    }
    saveProperties();
  }

  /**
   * Returns the host, if any.
   *
   * @param type	the type of proxy
   * @return		the host, empty string if none available
   */
  public static String getProxyHost(ProxyType type) {
    Properties	props;

    props = getProperties();
    switch (type) {
      case HTTP:
	return props.getProperty("http.proxyHost", "");
      case FTP:
	return props.getProperty("ftp.proxyHost", "");
      default:
        throw new IllegalStateException("Unhandled proxy type: " + type);
    }
  }

  /**
   * Returns the port, if any.
   *
   * @param type	the type of proxy
   * @return		the port, -1 if none available
   */
  public static int getProxyPort(ProxyType type) {
    Properties	props;

    props = getProperties();
    switch (type) {
      case HTTP:
	return Integer.parseInt(props.getProperty("http.proxyPort", "-1"));
      case FTP:
	return Integer.parseInt(props.getProperty("ftp.proxyPort", "-1"));
      default:
        throw new IllegalStateException("Unhandled proxy type: " + type);
    }
  }

  /**
   * Initializes the proxy from the stored settings, if necessary.
   *
   * @param request 	the request to apply the proxy settings to
   * @return 		the request itself
   * @see		#m_Invalid
   */
  public static Request applyProxy(Request request) {
    String	host;
    int		port;

    if (!m_Invalid)
      return request;

    for (ProxyType type: ProxyType.values()) {
      host = getProxyHost(type);
      port = getProxyPort(type);
      if (host.isEmpty() || (port == -1))
	continue;
      switch (type) {
        case FTP:
        case HTTP:
          request.proxy(new Proxy(Proxy.Type.DIRECT, new InetSocketAddress(host, port)));
      }
    }

    m_Invalid = false;

    return request;
  }

  /**
   * Turns the proxy type into its enum representation, if possible.
   *
   * @param type	the type string
   * @return		the enum, null if failed
   */
  public static ProxyType strToType(String type) {
    try {
      return ProxyType.valueOf(type.toUpperCase());
    }
    catch (Exception e) {
      return null;
    }
  }
}
