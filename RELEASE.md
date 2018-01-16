How to make a release
=====================

* Run the following command to deploy the artifact:

  ```
  mvn release:clean release:prepare release:perform
  ```

* Push all changes
* Update documentation

  * add new release link (`releases.md`)
  * update artifact version (`maven.md`)
  * test 
    
    ```
    mkdocs build --clean && mkdocs serve
    ```
    
  * deploy

    ```
    mkdocs gh-deploy --clean
    ```