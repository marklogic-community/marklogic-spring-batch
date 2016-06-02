# Overview
The vision of the MarkLogic Spring Batch (MSB) project is to provide the **BEST** solution for building batch processing jobs for the MarkLogic platform.  There are two main objectives of the MarkLogic Spring Batch project.  The first goal is to provide enhancements to the base [Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) framework that makes it easier to create batch processing programs using MarkLogic.  The second goal is to create a library of Spring Batch processing jobs that can be used on other projects, extended into more complex batch processing jobs, and used as a learning tool for developers.  
  
An objective for this project is to provide comprehensive testing for each enhancement to Spring Batch and for every batch processing job in the library.  Once your development environment is set up, you can use Gradle to execute the test suite and confirm operational readiness. 

# Installation

## Prerequisites
* [MarkLogic 8+](http://developer.marklogic.com/products)
* Java Development Kit 1.8
* Background understanding of the following technology
    * [Gradle](http://gradle.org/) and the [MarkLogic Gradle Plugin](http://developer.marklogic.com/code/ml-gradle)
    * [Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/)
    * [MarkLogic Java Client API](http://developer.marklogic.com/products/java)
    * [MarkLogic JUnit Library](https://github.com/rjrudin/ml-junit)


## Deploy the MarkLogic [JobRepository](http://docs.spring.io/spring-batch/trunk/reference/html/domain.html#domainJobRepository)
Out of the box, Spring Batch requires a relational database to persist the metadata asssociated with running a Spring Batch job.  The MarkLogic Spring Batch project provides a JobRepository that uses MarkLogic to persist this data.  These steps help with creating a JobRepository database and a test database for the sample jobs.  

1. Review the connection properties listed in [gradle.properties](https://github.com/sastafford/marklogic-spring-batch/blob/master/gradle.properties).
1. Run the following command

````
cd core
./gradlew mlDeploy
````

## Execute Tests
To confirm your environment setup, run the following command to run the unit test suite.  If all tests pass then everything is setup correctly.  

1. Run integration and unit tests
````
cd ..
./gradlew test
````

1. The test reports can be reviewed at core/build/reports/tests/index.html and jobs/build/reports/tests/index.html.  If you get failures please contact @sastafford. 
 
