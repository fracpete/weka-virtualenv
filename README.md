# weka-virtualenv

Virtual environment manager for Weka.

Mainly for launching the GUI, no support for running arbitrary classes
from command-line.


## Commands

Available commands:

```
create
	Creates a new environment.
delete
	Deletes an existing environment.
experimenter
	Launches the Weka Experimenter.
explorer
	Launches the Weka Explorer.
guichooser
	Launches the Weka GUIChooser.
help
	Outputs help information.
list_cmds
	Lists all available commands.
list_envs
	Lists all available environments.
status
	Outputs some status information.

Use -h/--help as argument to specific command to see further details.
```

## Examples

Create an environment for Weka 3.8.1:
```bash
wekavirtualenv.sh create \
  -n weka381 \
  -w /home/fracpete/programs/weka/weka-3-8-1/weka.jar
```

Create an environment for Weka 3.9.1 with a custom java binary to use
and 4GB of heap size:
```bash
wekavirtualenv.sh create \
  -n weka391 \
  -w /home/fracpete/programs/weka/weka-3-9-1/weka.jar \
  -j /home/fracpete/programs/jdk/jdk1.8.0_144-64bit/bin/java \
  -m 4g
```

Launch the GUIChooser from the `weka381` environment:
```bash
wekavirtualenv.sh guichooser -n weka381
```

Launch the Explorer from the `weka391` environment:
```bash
wekavirtualenv.sh explorer -n weka391
```

For Windows users, use `wekavirtualenv.bat` instead of `wekavirtualenv.sh`
from the `bin` directory.


## Releases

The following releases are available:

* [0.0.2](https://github.com/fracpete/weka-virtualenv/releases/download/weka-virtualenv-0.0.2/weka-virtualenv-0.0.2-bin.zip)
