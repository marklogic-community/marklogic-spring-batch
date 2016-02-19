# Installation

## Prerequisites
* [MarkLogic Server](http://developer.marklogic.com/products)
* Java Development Kit 1.8

## Deploy MarkLogic Spring Batch Admin
1. Review the connection properties listed in [application.properties](https://github.com/sastafford/marklogic-spring-batch/blob/master/src/main/resources/config/application.properties).  

1. Set up your MarkLogic environment.  MarkLogic deployment uses [Gradle](http://gradle.org/) and the [MarkLogic Gradle Plugin](http://developer.marklogic.com/code/ml-gradle). 
````
./gradlew mlDeploy
````

## Execute Tests
1. Run integration and unit tests
````
./gradlew test
````

1. If all tests passed, then you are setup and ready to go!  The test report can be reviewed at build/reports/tests/index.html for full test report.  If you get failures please contact @sastafford. 
 
