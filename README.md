# weka-virtualenv

Virtual environment manager for Weka, inspired by the extremely useful virtual 
environments that Python supports.

*weka-virtualenv* be used for launching the GUI or arbitrary Weka classes. 

Since *weka-virtualenv* uses options starting with double-dashes (`--`), clashes with 
Weka options are avoided. Any option that wasn't consumed by *weka-virtualenv* 
will get further processed by the command. E.g., when launching the Explorer
using the `explorer` command, a dataset can be supplied to load immediately, 
or, when executing a classifier using the `run` command, any additional option 
will get passed to the Weka class.

At this stage, no graphical user interface available for managing or launching 
environments.


## Commands

```
Available commands:

create <options>
	Creates a new environment.
delete <options>
	Deletes an existing environment.
experimenter <env>
	Launches the Weka Experimenter.
explorer <env>
	Launches the Weka Explorer.
guichooser <env>
	Launches the Weka GUIChooser.
help
	Outputs help information.
list_cmds
	Lists all available commands.
list_envs
	Lists all available environments.
run <env> <options>
	Executes an arbitrary class with the left-over command-line options.
status
	Outputs some status information.

Notes:
<env>
	the name of the environment to use for the command.
<options>
	the command supports additional options,
	you can use --help as argument to see further details.
```

## Examples

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

Launch the GUIChooser from the `weka381` environment:
```bash
wenv.sh guichooser weka381
```

Launch the Explorer from the `weka391` environment:
```bash
wenv.sh explorer weka391
```

Cross-validate J48 from the `weka381` environment on the *iris* dataset:
```bash
wenv.sh run weka381 --class weka.classifiers.trees.J48 \
  -t /home/fracpete/development/datasets/uci/nominal/iris.arff
```

For Windows users, use `wenv.bat` instead of `wenv.sh` from the `bin` directory.


## Environment locations

The environments get created in the following directory:

* Unix (Linux, Mac)

  `$HOME/.local/share/wekavirtualenv`

* Windows

  `%USERPROFILE%\wekavirtualenv`


## Releases

The following releases are available:

* [0.0.3](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.3/weka-virtualenv-0.0.3-bin.zip)
* [0.0.2](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.2/weka-virtualenv-0.0.2-bin.zip)
