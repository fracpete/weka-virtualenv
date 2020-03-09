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
 * Environment.java
 * Copyright (C) 2017-2020 University of Waikato, Hamilton, NZ
 */

package com.github.fracpete.wekavirtualenv.env;

import com.github.fracpete.wekavirtualenv.command.Command;
import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import nz.ac.waikato.cms.core.FileUtils;
import nz.ac.waikato.cms.jenericcmdline.core.OptionUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Setup information of an environment.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Environment
  implements Cloneable, Comparable<Environment> {

  /** the name of the environment. */
  public final static String KEY_NAME = "name";

  /** the java binary (full path). */
  public final static String KEY_JAVA = "java";

  /** the heap size. */
  public final static String KEY_MEMORY = "memory";

  /** the jvm parameters. */
  public final static String KEY_JVMPARAMS = "jvmparams";

  /** the weka jar to use. */
  public final static String KEY_WEKA = "weka";

  /** the environment variables to set. */
  public final static String KEY_ENVVARS = "envvars";

  /** the comment. */
  public final static String KEY_COMMENT = "comment";

  /** whether to use the package manager in offline mode. */
  public final static String KEY_PKGMGR_OFFLINE = "pkgmgr_offline";

  public static final String DEFAULT = "<default>";

  public static final String NONE = "<none>";

  /** the name of the environment. */
  public String name;

  /** the java binary ("" if default). */
  public String java;

  /** the heap size ("" if default). */
  public String memory;

  /** the jvm parameters ("" if default). */
  public String jvmparams;

  /** the weka jar (full path). */
  public String weka;

  /** the comment. */
  public String comment;

  /** the additional environment variables (blank-separated key=value pairs). */
  public String envvars;

  /** whether to use the package manager offline. */
  public boolean pkgMgrOffline;

  /**
   * Returns a clone.
   *
   * @return		the clone
   */
  public Environment clone() {
    Environment	result;

    result               = new Environment();
    result.name          = name;
    result.java          = java;
    result.memory        = memory;
    result.jvmparams     = jvmparams;
    result.weka          = weka;
    result.envvars       = envvars;
    result.comment       = comment;
    result.pkgMgrOffline = pkgMgrOffline;

    return result;
  }

  /**
   * Returns the Weka version of the jar, if possible.
   *
   * @return		the version
   */
  public String version() {
    return getVersion(weka);
  }

  /**
   * Comparison based on name alone.
   *
   * @param o		the other environment
   * @return		the result of the string comparison of the names
   */
  @Override
  public int compareTo(Environment o) {
    return name.compareTo(o.name);
  }

  /**
   * Checks whether the object is an environment with the same name.
   *
   * @param obj		the other object
   * @return		true if other object an environment with same name
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof Environment) && (compareTo((Environment) obj) == 0);
  }

  /**
   * Returns the environment in simple string representation.
   *
   * @return		the generated string
   */
  public String toString() {
    return toString("", false);
  }

  /**
   * Looks for the search string in its parameters whether they contain this
   * sub-string (case-insensitive).
   *
   * @param search 	the search string
   * @return		true if match
   */
  public boolean matches(String search) {
    search = search.toLowerCase();
    return name.toLowerCase().contains(search)
      || java.toLowerCase().contains(search)
      || memory.toLowerCase().contains(search)
      || ((jvmparams != null) && jvmparams.toLowerCase().contains(search))
      || weka.toLowerCase().contains(search)
      || ((envvars != null) && envvars.toLowerCase().contains(search))
      || ((comment != null) && comment.toLowerCase().contains(search));
  }

  /**
   * Returns the environment in simple string representation.
   *
   * @param prefix	the prefix to use, eg indentation
   * @return		the generated string
   */
  public String toString(String prefix, boolean verbose) {
    StringBuilder	result;
    String		version;

    result = new StringBuilder();
    result.append(prefix).append("Name: ").append(name).append("\n");
    result.append(prefix).append("Java: ").append(java.isEmpty() ? DEFAULT : java).append("\n");
    result.append(prefix).append("Memory: ").append(memory.isEmpty() ? DEFAULT : memory).append("\n");
    result.append(prefix).append("JVM params: ").append((jvmparams == null) || jvmparams.isEmpty() ? NONE : jvmparams).append("\n");
    result.append(prefix).append("Weka: ").append(weka).append("\n");
    result.append(prefix).append("Env. vars: ").append((envvars == null) || envvars.isEmpty() ? NONE : envvars).append("\n");
    result.append(prefix).append("Comment: ").append((comment == null) || comment.isEmpty() ? NONE : comment).append("\n");
    result.append(prefix).append("PkgMgr offline: ").append(pkgMgrOffline).append("\n");
    if (verbose) {
      version = version();
      if (version == null)
        version = "?.?.?";
      result.append(prefix).append("Version: ").append(version).append("\n");
      result.append(prefix).append("Dir: ").append(Environments.getEnvDir(name)).append("\n");
    }

    return result.toString();
  }

  /**
   * Splits the environment variables string into a map.
   *
   * @param cmd		the command context
   * @return		the generated map
   */
  public Map<String,String> envvarsToMap(Command cmd) {
    Map<String,String>	result;
    String[]		vars;
    String[]		parts;

    result = new HashMap<>();

    if ((envvars != null) && !envvars.isEmpty()) {
      cmd.println("Using environment variables: " + envvars, true);
      try {
	vars = OptionUtils.splitOptions(envvars);
	for (String var : vars) {
	  if (var.contains("=")) {
	    parts = new String[2];
	    parts[0] = var.substring(0, var.indexOf("="));
	    parts[1] = var.substring(var.indexOf("=") + 1);
	    result.put(parts[0], parts[1]);
          }
	  else {
            cmd.println("Wrong format for environment variable (key=value)? " + var, true);
          }
	}
      }
      catch (Exception e) {
        cmd.println("Failed to parse environment variables (blank separated list, key=value pairs): " + envvars, e);
      }
    }

    return result;
  }

  /**
   * Reads the environment definition.
   *
   * @param file 	the definition props file
   * @return		the environment, null if failed to read or invalid
   */
  public static Environment read(File file) {
    Environment result;
    FileReader freader;
    Properties props;

    result = null;

    if (!file.exists())
      return null;

    freader = null;
    try {
      freader = new FileReader(file);
      props   = new Properties();
      props.load(freader);
      if ((props.getProperty(KEY_NAME) != null) && (props.getProperty(KEY_WEKA) != null)) {
        result               = new Environment();
        result.name          = props.getProperty(KEY_NAME);
        result.java          = props.getProperty(KEY_JAVA, "");
        result.memory        = props.getProperty(KEY_MEMORY, "");
        result.jvmparams     = props.getProperty(KEY_JVMPARAMS, "");
        result.weka          = props.getProperty(KEY_WEKA);
        result.envvars       = props.getProperty(KEY_ENVVARS, "");
        result.comment       = props.getProperty(KEY_COMMENT, "");
        result.pkgMgrOffline = props.getProperty(KEY_PKGMGR_OFFLINE, "false").equalsIgnoreCase("true");
      }
    }
    catch (Exception e) {
      System.err.println("Failed to read environment: " + file);
      e.printStackTrace();
    }
    finally {
      FileUtils.closeQuietly(freader);
    }

    return result;
  }

  /**
   * Saves the environment to the specified file.
   *
   * @param env 	the environment to save
   * @param file	the file to save to
   * @return		null if successful, otherwise error message
   */
  public static String write(Environment env, File file) {
    String		result;
    Properties		props;
    FileWriter		fwriter;
    BufferedWriter	bwriter;

    result = null;

    props = new Properties();
    props.setProperty(KEY_NAME, env.name);
    props.setProperty(KEY_JAVA, (env.java == null ? "" : env.java));
    props.setProperty(KEY_MEMORY, (env.memory == null ? "" : env.memory));
    props.setProperty(KEY_JVMPARAMS, (env.jvmparams == null ? "" : env.jvmparams));
    props.setProperty(KEY_WEKA, env.weka);
    props.setProperty(KEY_ENVVARS, (env.envvars == null ? "" : env.envvars));
    props.setProperty(KEY_COMMENT, (env.comment == null ? "" : env.comment));
    props.setProperty(KEY_PKGMGR_OFFLINE, "" + env.pkgMgrOffline);

    fwriter = null;
    bwriter = null;
    try {
      fwriter = new FileWriter(file);
      bwriter = new BufferedWriter(fwriter);
      props.store(bwriter, null);
    }
    catch (Exception e) {
      result = "Failed to save environment to: " + file + "\n" + e;
    }
    finally {
      FileUtils.closeQuietly(bwriter);
      FileUtils.closeQuietly(fwriter);
    }

    return result;
  }

  /**
   * Extracts the version from the Weka jar, if possible.
   *
   * @param jar		the jar to analyze
   * @return		the version, null if failed to extract
   */
  public static String getVersion(String jar) {
    return getVersion(jar, false);
  }

  /**
   * Extracts the version from the Weka jar, if possible.
   *
   * @param jar		the jar to analyze
   * @return		the version, null if failed to extract
   */
  public static String getVersion(String jar, boolean verbose) {
    String		result;
    byte[]		bytes;

    bytes = readResource(jar, "weka/core/version.txt", verbose);
    if (bytes == null)
      result = null;
    else
      result = new String(bytes);

    return result;
  }

  /**
   * Reads the binary content of the resource from the jar file.
   *
   * @param jar		the jar to use
   * @param resource 	the resource to load
   * @return		the content, null if failed to load
   */
  public static byte[] readResource(String jar, String resource, boolean verbose) {
    TByteList 		result;
    JarFile		jfile;
    JarEntry 		entry;
    InputStream		in;
    int 		b;

    result = new TByteArrayList();
    in     = null;
    jfile  = null;
    try {
      jfile   = new JarFile(jar);
      entry   = jfile.getJarEntry(resource);
      in      = jfile.getInputStream(entry);
      while ((b = in.read()) != -1)
	result.add((byte) b);
    }
    catch (Exception e) {
      if (verbose) {
        System.err.println("Failed to read jar: " + jar);
        e.printStackTrace();
      }
      result = null;
    }
    finally {
      FileUtils.closeQuietly(in);
      if (jfile != null) {
        try {
	  jfile.close();
	}
	catch (Exception e) {
          // ignored
	}
      }
    }

    return result.toArray();
  }

  /**
   * Checks whether the given class is available.
   *
   * @param jar		the jar to analyze
   * @param classname	the class to look for
   * @return		true if present
   */
  public static boolean hasClass(String jar, String classname, boolean verbose) {
    return hasResource(jar, classname.replace(".", "/") + ".class", verbose);
  }

  /**
   * Checks whether the given resource is available.
   *
   * @param jar		the jar to analyze
   * @param resource	the resource to look for
   * @return		true if present
   */
  public static boolean hasResource(String jar, String resource, boolean verbose) {
    boolean		result;
    JarFile		jfile;
    JarEntry 		entry;

    jfile = null;
    try {
      jfile  = new JarFile(jar);
      entry  = jfile.getJarEntry(resource);
      result = (entry != null);
    }
    catch (Exception e) {
      if (verbose) {
        System.err.println("Failed to read jar: " + jar);
        e.printStackTrace();
      }
      result = false;
    }
    finally {
      if (jfile != null) {
        try {
	  jfile.close();
	}
	catch (Exception e) {
          // ignored
	}
      }
    }

    return result;
  }
}
