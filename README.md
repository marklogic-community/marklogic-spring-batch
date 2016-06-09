# MarkLogic Spring Batch

The vision of the MarkLogic Spring Batch (MSB) project is to provide the **BEST** solution for building batch processing jobs for the MarkLogic platform.  There are three goals of the MarkLogic Spring Batch project.  The first is to enhancements the core [Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) framework that makes it easy to create batch processing programs using MarkLogic.  The second goal is to create a library of batch processing jobs that are commonly executed.  The third goal is to provide examples for writing custom batch processing programs that utilize MarkLogic.     

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisities

What things you need to install the software and how to install them

* [MarkLogic 8+](http://developer.marklogic.com/products)
* [Java Development Kit 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Optional: [Gradle 2.+](http://gradle.org/gradle-download/)

### Background Info
* [Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/)
* [Spring Framework](https://projects.spring.io/spring-framework/)
* [Gradle](http://gradle.org/) and the [MarkLogic Gradle Plugin](http://developer.marklogic.com/code/ml-gradle)
* [MarkLogic Java Client API](http://developer.marklogic.com/products/java)
* [MarkLogic Java Client Util](https://github.com/rjrudin/ml-javaclient-util)
* [MarkLogic JUnit Library](https://github.com/rjrudin/ml-junit)
* [MarkLogic App Deployer](https://github.com/rjrudin/ml-app-deployer)

### Assumptions
* The MarkLogic host is **localhost**
* Port **8200** is available for use and no other applications are listening on that port

If either of these assumptions is not true then review the following gradle.properties files and change appropriately.
* [./gradle.properties]()
* [./core/gradle.properties]()
* [./jobs/gradle.properties]()

_NOTE: There is an open issue to address the multiple gradle.properties issues_

### Installing

Create the MarkLogic Job Repository application.  The appserver and database that is created will also serve as the target database for all testing. 

```
gradle deployMarkLogicJobRepository
```


## Running the tests

To verify that everything works, run through all the tests.

```
gradle test
```

Tests can be verified in the following two reports

* Core Library: ./core/build/reports/tests/index.html
* Jobs Library: ./jobs/build/reports/tests/index.html

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

To deploy the MarkLogic Jobs utility, execute the following gradle command

```
gradle :jobs:distZip
```

This will create the distribution archive file under ./jobs/build/distribution/jobs.zip

## Built With

* Dropwizard - Bla bla bla
* Maven - Maybe
* Atom - ergaerga

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc



-----


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
 
