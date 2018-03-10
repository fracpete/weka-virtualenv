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


## Links

* [Releases](https://fracpete.github.io/weka-virtualenv/releases/)
* [Documentation](https://fracpete.github.io/weka-virtualenv/)
* [Videos](https://www.youtube.com/playlist?list=PLMeTbrv9G0apt1ii_kyU5rtJEu58WrGwB)


## Licenses

The scripts are licensed under [Apache 2.0](https://github.com/fracpete/weka-virtualenv/blob/master/APACHE.txt) 
and all other source code under [GPL 3.0](https://github.com/fracpete/weka-virtualenv/blob/master/GPL.txt).
