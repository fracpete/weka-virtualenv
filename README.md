# weka-virtualenv

Virtual environment manager for Weka, inspired by the extremely useful virtual 
environments that Python supports.

*weka-virtualenv* can be used for launching the GUI or arbitrary Weka classes.

Since *weka-virtualenv* uses options starting with double-dashes (`--`), clashes with 
Weka options are avoided. Any option that wasn't consumed by *weka-virtualenv* 
will get further processed by the command. E.g., when launching the Explorer
using the `explorer` command, a dataset can be supplied to load immediately, 
or, when executing a classifier using the `run` command, any additional option 
will get passed to the Weka class.

You can use the tool either through the command-line or through its user 
interface.


**Note**
You still need to install Weka yourself, *weka-virtualenv* only helps you
separating your various Weka installations. It is mainly aimed at separating
packages.

## How it works

The tool simply uses existing Weka functionality, namely the `WEKA_HOME`
environment variable, for separating the various environments. 
When launching a class or GUI tool, it launches a new Java process with the
`WEKA_HOME` environment variable pointing to the current environment's
`wekafiles` directory. 

If an environment has no Java or heap size configure (`<default>`), then it 
just uses the system's default settings for launching the new process.


## User interface

You can start the GUI with the following script from the `bin` directory:

* Linux/Mac: `wenvg.sh`
* Windows: `wenvg.bat`


## Commands

```
Available commands:

alias-add <options> <args>
	Adds an alias definition, i.e., shortcut for command and options.
	All options not consumed by this command will get used as options for the alias.
	No checks are being performed on the correctness.

alias-del <options>
	Removes an alias definition.

alias-exec <options> <args>
	Executes an alias definition, i.e., shortcut for command and options.
	All options not consumed by this command will get used as additional options for the alias.
	No checks are being performed on the correctness.

alias-list <options>
	Lists aliases and their associated options.
	Listing can be for global aliases, per environment, or for all.

arffviewer <env> <args>
	Launches the Weka Arff viewer.
	You can supply dataset filenames to load immediately in the viewer.

clone <options>
	Clones an existing environment.
	Allows adjusting of environment parameters.

create <options>
	Creates a new environment.
	Can be initialized with the content of an existing 'wekafiles' directory.

delete <options>
	Deletes an existing environment.

experimenter <env> <args>
	Launches the Weka Experimenter.

explorer <env> <args>
	Launches the Weka Explorer.
	You can supply a dataset filename to load immediately in the Explorer.

guichooser <env>
	Launches the Weka GUIChooser.

help
	Outputs help information.

knowledgeflow <env> <args>
	Launches the Weka KnowledgeFlow.
	You can supply a flow file to load immediately.

list_cmds
	Lists all available commands.

list_envs <options>
	Lists all available environments.

pkgmgr <env> <args>
	Executes the commandline package manager.
	You can supply additional options to the package manager, like '-list-packages'.

pkgmgr-gui <env>
	Launches the package manager user interface.

reset <options>
	Deletes an existing environment, i.e., deletes the "wekafiles" sub-directory.

run <env> <options> <args>
	Executes an arbitrary class with the unconsumed command-line options.

sqlviewer <env>
	Launches the Weka SQL Viewer.

status
	Outputs some status information.

update <env> <options>
	Allows adjusting of parameters of an existing environment.

workbench <env>
	Launches the Weka Workbench.


Notes:
<env>
	the name of the environment to use for the command.
<options>
	the command supports additional options,
	you can use --help as argument to see further details.
<args>
	the command passes on all unconsumed options to the 
	underlying process
```

## Examples

**Note for Windows users:** Use `wenv.bat` instead of `wenv.sh` from the `bin` 
directory for the following examples. Also, remove the trailing backslashes
in the commands and place the whole command on a single line.


### Environments

Create an environment for Weka 3.8.1:
```bash
wenv.sh create \
  --name weka381 \
  --weka /home/fracpete/programs/weka/weka-3-8-1/weka.jar
```

Create an environment for Weka 3.9.1 with a custom java binary to use
and 4GB of heap size:
```bash
wenv.sh create \
  --name weka391 \
  --weka /home/fracpete/programs/weka/weka-3-9-1/weka.jar \
  --java /home/fracpete/programs/jdk/jdk1.8.0_144-64bit/bin/java \
  --memory 4g
```

### Launching user interfaces

Launch the GUIChooser from the `weka381` environment:
```bash
wenv.sh guichooser weka381
```

Launch the Explorer from the `weka391` environment:
```bash
wenv.sh explorer weka391
```

### Executing classes

Cross-validate J48 from the `weka381` environment on the *iris* dataset:
```bash
wenv.sh run weka381 --class weka.classifiers.trees.J48 \
  -t /home/fracpete/development/datasets/uci/nominal/iris.arff
```

### Aliases (global)

Create a global alias called `j48`:
```bash
wenv.sh alias-add --name j48 run --class weka.classifiers.trees.J48 -C 0.3
``` 

If the command of a global alias requires an environment for executing, then
the environment needs to get injected via the `--inject-env` option. 
The following command executes the global alias `j48`, cross-validating the 
`J48` classifier on the UCI dataset *iris*:
```bash
wenv.sh alias-exec --inject-env weka381 --name j48 -t iris.arff
```

### Aliases (environment-specific)

Create an alias called `j48` in the `weka381` environment:
```bash
wenv.sh alias-add --env weka381 --name j48 run --class weka.classifiers.trees.J48 -C 0.3
``` 
 
The following command executes the alias `j48` from the `weka381` environment, 
cross-validating the `J48` classifier on the UCI dataset *iris*:
```bash
wenv.sh alias-exec --env weka381 --name j48 -t iris.arff
```

### Aliases (list)

You can list all aliases, global and for all environments, as follows:
```bash
wenv.sh alias-list --all
```

Which will output something like this:
```
Environment | Name | Command                                      
------------+------+----------------------------------------------
<global>    | j48  | run --class weka.classifiers.trees.J48 -C 0.3
weka381     | j48  | run -class weka.classifiers.trees.J48 -C 0.3 
```

You can list all global aliases as follows:
```bash
wenv.sh alias-list
```

You can list the aliases for environment `weka381` as follows:
```bash
wenv.sh alias-list --env weka381
```


## Environment locations

The environments get created in the following directory:

* Unix (Linux, Mac)

  `$HOME/.local/share/wekavirtualenv`

* Windows

  `%USERPROFILE%\wekavirtualenv`


## Releases

The following releases are available:

* [0.0.10](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.10/weka-virtualenv-0.0.10-bin.zip)
* [0.0.9](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.9/weka-virtualenv-0.0.9-bin.zip)
* [0.0.8](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.8/weka-virtualenv-0.0.8-bin.zip)
* [0.0.7](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.7/weka-virtualenv-0.0.7-bin.zip)
* [0.0.4](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.4/weka-virtualenv-0.0.4-bin.zip)
* [0.0.3](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.3/weka-virtualenv-0.0.3-bin.zip)
* [0.0.2](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.2/weka-virtualenv-0.0.2-bin.zip)


## Maven

Add the following dependency to you `pom.xml`:
```xml
  <dependency>
    <groupId>com.github.fracpete</groupId>
    <artifactId>weka-virtualenv</artifactId>
    <version>0.0.10</version>
  </dependency>
```


## Licenses

The scripts are licensed under [Apache 2.0](https://github.com/fracpete/weka-virtualenv/blob/master/APACHE.txt) 
and all other source code under [GPL 3.0](https://github.com/fracpete/weka-virtualenv/blob/master/GPL.txt).