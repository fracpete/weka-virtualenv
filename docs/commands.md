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
