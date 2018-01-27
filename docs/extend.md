It is quite easy to add new commands to the tool:

* create a class derived from `com.github.fracpete.wekavirtualenv.command.AbstractCommand`
* place the class in package `com.github.fracpete.wekavirtualenv.command`


# Dataset filenames
If your command should handle additional arguments as dataset filenames, then
implement the indicator interface `com.github.fracpete.wekavirtualenv.command.DatasetHandler`.

This interface is used by the `com.github.fracpete.wekavirtualenv.gui.ArffCommandSelector`
tool, listing all the tools in the dropdown box that implement this interface.
By default, these are by default only the *Explorer* and *ArffViewer*.  


# Handling arguments
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


# Executing class with main method
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
