How to make a release
=====================

* Run the following command to deploy the artifact:

  ```
  mvn release:clean release:prepare release:perform
  ```

* Push all changes
* Update documentation - if necessary

  * test 
    
    ```
    mkdocs build --clean && mkdocs serve
    ```
    
  * deploy

    ```
    mkdocs gh-deploy --clean
    ```