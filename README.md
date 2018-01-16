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


## Documentation

You can find general documentation and examples here:

[fracpete.github.io/weka-virtualenv](https://fracpete.github.io/weka-virtualenv/)


## Licenses

The scripts are licensed under [Apache 2.0](https://github.com/fracpete/weka-virtualenv/blob/master/APACHE.txt) 
and all other source code under [GPL 3.0](https://github.com/fracpete/weka-virtualenv/blob/master/GPL.txt).
