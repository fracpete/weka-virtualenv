**Note for Windows users:** Use `wenv.bat` instead of `wenv.sh` from the `bin` 
directory for the following examples. Also, remove the trailing backslashes
in the commands and place the whole command on a single line.


## Environments

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

## Launching user interfaces

Launch the GUIChooser from the `weka381` environment:
```bash
wenv.sh guichooser weka381
```

Launch the Explorer from the `weka391` environment:
```bash
wenv.sh explorer weka391
```

## Executing classes

Cross-validate J48 from the `weka381` environment on the *iris* dataset:
```bash
wenv.sh run weka381 --class weka.classifiers.trees.J48 \
  -t /home/fracpete/development/datasets/uci/nominal/iris.arff
```

## Aliases (global)

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

## Aliases (environment-specific)

Create an alias called `j48` in the `weka381` environment:
```bash
wenv.sh alias-add --env weka381 --name j48 run --class weka.classifiers.trees.J48 -C 0.3
``` 
 
The following command executes the alias `j48` from the `weka381` environment, 
cross-validating the `J48` classifier on the UCI dataset *iris*:
```bash
wenv.sh alias-exec --env weka381 --name j48 -t iris.arff
```

## Aliases (list)

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

# Environment locations

The environments get created in the following directory:

* Unix (Linux, Mac)

    ```
    $HOME/.local/share/wekavirtualenv
    ```

* Windows

    ```
    %USERPROFILE%\wekavirtualenv
    ```
