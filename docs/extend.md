# Automatic class discovery
Thanks to the automatic class discovery (provided by 
[jclasslocator](https://github.com/Waikato/jclasslocator)), you don't have
to register anything, you only have to place the command in the same package
as the abstract superclass or interface.


# Command-line
It is quite easy to add new commands to the tool:

* create a class derived from `com.github.fracpete.wekavirtualenv.command.AbstractCommand`
* place the class in package `com.github.fracpete.wekavirtualenv.command`

## Dataset filenames
If your command should handle additional arguments as dataset filenames, then
implement the indicator interface `com.github.fracpete.wekavirtualenv.command.DatasetHandler`.

This interface is used by the `com.github.fracpete.wekavirtualenv.gui.ArffCommandSelector`
tool, listing all the tools in the dropdown box that implement this interface.
By default, these are by default only the *Explorer* and *ArffViewer*.  

## Handling arguments
The `Delete` command just takes a single option, which is the name of the
environment to delete. In order for the parsing to work, you have to define
a `ArgumentParser` object in the `getParser` method and then access the
parsed options in the `doExecute` method, using the `Namespace` object.

Here is the `Delete` command's code:
```java
public class Delete extends AbstractCommand {

  public String getName() {
    return "delete";
  }

  public String getHelp() {
    return "Deletes an existing environment.";
  }

  public ArgumentParser getParser() {
    ArgumentParser result = new ArgumentParser(getName());
    result.addOption("--name")
      .dest("name")
      .help("the name of the environment to delete")
      .required(true);
    return result;
  }

  protected boolean doExecute(Namespace ns, String[] options) {
    String msg = Environments.delete(ns.getString("name"));
    if (msg != null)
      addError("Failed to delete environment '" + ns.getString("name") + "':\n" + msg);
    else
      System.out.println("Environment successfully deleted: " + ns.getString("name"));
    return (msg == null);
  }
}
``` 

For more information on the parsing, check out the 
[simple-argparse4j](https://github.com/fracpete/simple-argparse4j)
project page.


## Executing class with main method
The `AbstractLaunchCommand` class makes it easier to execute a class that
has a `main` method.

The class for launching the Weka Explorer is quite simple:

```java
public class Explorer extends AbstractLaunchCommand implements DatasetHandler {

  public String getName() {
    return "explorer";
  }

  public String getHelp() {
    return "Launches the Weka Explorer.\n"
      + "You can supply a dataset filename to load immediately in the Explorer.";
  }

  public boolean supportsAdditionalArguments() {
    return true;
  }

  protected boolean doExecute(Namespace ns, String[] options) {
    return launch(build("weka.gui.explorer.Explorer", options));
  }
}

```

In the `doExecute` method, the `build` method takes the class name and any
options that the class should process. In the Explorer's case an optional 
dataset to load.


# Script command
If you want to add a script command, you just have to derive it from the
`com.github.fracpete.wekavirtualenv.command.script.AbstractScriptCommand`
super class and place it in the `com.github.fracpete.wekavirtualenv.command.script`
package.

Here is the code for the `DirName` command:
```java
public class DirName extends AbstractScriptCommand {

  public String getName() {
    return "dir_name";
  }

  public String getHelp() {
    return "Extracts the path from the specified file variable.";
  }

  public ArgumentParser getParser() {
    ArgumentParser result = new ArgumentParser(getName());
    result.addOption("--file")
      .dest("file")
      .help("the full path to extract the path from.")
      .required(true);
    result.addOption("--dest")
      .dest("dest")
      .help("the name of the variable to store the result in.")
      .required(true);
    return result;
  }

  protected boolean evalCommand(Namespace ns, String[] options) {
    File file = new File(ns.getString("file"));
    getContext().getVariables().set(ns.getString("dest"), file.getParentFile().getAbsolutePath());
    return true;
  }
}
``` 


# Filter
If you want to add a custom output filter (like grep or tee), then you only
have to implement the `com.github.fracpete.wekavirtualenv.command.filter.Filter`
interface (or use the abstract superclass `AbstractFilter` for convenience)
and place it in the `com.github.fracpete.wekavirtualenv.command.filter` package.

Here is the code for the `Grep` filter:
```java
public class Grep
  extends AbstractFilter {

  /** the pattern for matching. */
  protected Pattern m_RegExp;

  protected boolean m_Invert;

  public String getName() {
    return "grep";
  }

  @Override
  public String getHelp() {
    return "For capturing strings that match a regular expression.";
  }

  public ArgumentParser getParser() {
    ArgumentParser result = super.getParser();
    result.addOption("--regexp")
      .dest("regexp")
      .help("the regular expression that the output must match to be kept.")
      .required(true);
    result.addOption("--invert")
      .dest("invert")
      .help("whether to invert the matching sense.")
      .argument(false);
    return result;
  }

  public boolean initialize(Namespace ns) {
    boolean result = super.initialize(ns);
    if (result) {
      try {
	m_RegExp = Pattern.compile(ns.getString("regexp"));
      }
      catch (Exception e) {
        addError("Invalid regular expression: " + ns.getString("regexp"), e);
        return false;
      }
      m_Invert = ns.getBoolean("invert");
    }
    return result;
  }

  protected String doIntercept(String line, boolean stdout) {
    if ((!m_Invert && m_RegExp.matcher(line).matches())
      || (m_Invert && !m_RegExp.matcher(line).matches())) {
      return line;
    }
    return null;
  }
}
```

# User interface
For adding a command in the user interface, you have to subclass the
abstract class `com.github.fracpete.wekavirtualenv.gui.command.AbstractGUICommand`
and place the class in the `com.github.fracpete.wekavirtualenv.gui.command` package.

If you want to capture the output of the process, then let the method
`generatesOutput()` return `true`.

If you the command is to be run in the context of a Weka environment, then
the method `requiresEnvironment()` needs to return `true`. 
Methods that return `true` automatically show up in the drop-down list of
an environment. Ones that return `false`, show up in the main menu.

The `destroy()` method is used for stopping any process that got launched.

Here is the code for launching the Weka Explorer, which requires an environment
and also captures the output of the launched process:

```java
public class Explorer
  extends AbstractGUICommand {

  protected com.github.fracpete.wekavirtualenv.command.Explorer m_Command;

  public String getName() {
    return "Explorer";
  }

  public String getGroup() {
    return "gui";
  }

  public boolean requiresEnvironment() {
    return true;
  }

  public boolean generatesOutput() {
    return true;
  }

  @Override
  protected String doExecute() {
    String result = null;
    m_Command = new com.github.fracpete.wekavirtualenv.command.Explorer();
    m_Command.setEnv(m_Environment);
    transferOutputListeners(m_Command);
    if (!m_Command.execute(new String[0])) {
      if (m_Command.hasErrors())
        result = m_Command.getErrors();
      else
        result = "Failed to launch Explorer!";
    }
    m_Command = null;
    return result;
  }

  public void destroy() {
    if (m_Command != null)
      m_Command.destroy();
  }
}
```