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

arffviewer <env> <args> | output filter(s)
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

echo <options> | output filter(s)
	Outputs the specified message.

experimenter <env> <args> | output filter(s)
	Launches the Weka Experimenter.

explorer <env> <args> | output filter(s)
	Launches the Weka Explorer.
	You can supply a dataset filename to load immediately in the Explorer.

filter_help <options>
	Prints help on the available output filters.

guichooser <env> | output filter(s)
	Launches the Weka GUIChooser.

help
	Outputs help information.

install <options>
	Downloads and installs a specific Weka version.
	NB: The downloaded zip file contains a sub-directory with the version of Weka.

knowledgeflow <env> <args> | output filter(s)
	Launches the Weka KnowledgeFlow.
	You can supply a flow file to load immediately.

list_cmds
	Lists all available commands.

list_envs <options>
	Lists all available environments.

pkgmgr <env> <args> | output filter(s)
	Executes the commandline package manager.
	You can supply additional options to the package manager, like '-list-packages'.

pkgmgr-gui <env> | output filter(s)
	Launches the package manager user interface.

reset <options>
	Deletes an existing environment, i.e., deletes the "wekafiles" sub-directory.

run <env> <options> <args> | output filter(s)
	Executes an arbitrary class with the unconsumed command-line options.

script <options>
	Executes the commands in the specified script file.
	Empty lines and lines starting with # get skipped.

script_help <options>
	Prints help on the available script commands.

sqlviewer <env> | output filter(s)
	Launches the Weka SQL Viewer.

status
	Outputs some status information.

update <env> <options>
	Allows adjusting of parameters of an existing environment.

workbench <env> | output filter(s)
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
| output filter(s)
	the command generates output which can filtered, 
	these filters can be chained, one '|' per filter
```
