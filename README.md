# What is Spring Batch?

[Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) provides reusable functions that are essential in processing large volumes of records, including logging/tracing, transaction management, job processing statistics, job restart, skip, and resource management. It also provides more advanced technical services and features that will enable extremely high-volume and high performance batch jobs through optimization and partitioning techniques. Simple as well as complex, high-volume batch jobs can leverage the framework in a highly scalable manner to process significant volumes of information.

# What is MarkLogic Spring Batch?
The vision of the MarkLogic Spring Batch (MSB) project is to provide the **BEST** solution for building custom batch processing jobs for the MarkLogic platform.  There are three goals of the MarkLogic Spring Batch project.  

* To provide recipes for writing custom batch processing programs that utilize MarkLogic.  Most data migration processes are very specific to one or more data sources.  You could use a generic batch processing job like loading files from a directory but typically a custom job is needed.  MarkLogic Spring Batch allows you to focus on the important business logic challenges and less on the operations.  
* To enhance the core [Spring Batch](http://docs.spring.io/spring-batch/apidocs/) framework that makes it easy to create batch processing programs using MarkLogic.  This represents the fusion of the MarkLogic Java Client API and the Spring Batch core libraries.  
* To create a library of common batch processing jobs that are executed against a MarkLogic database.  

The sub-projects are broken down into according to each goal.

* [examples](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples) - Starter templates for creating your own batch processing jobs (i.e. migrating from a RDBMS)
* [jobs] - A library of common MarkLogic batch processing jobs implemented as Spring Batch [Jobs](http://docs.spring.io/spring-batch/trunk/reference/html/domain.html#domainJob)
* [core] - Contains all enhancements on the Spring Batch framework

# Why use MarkLogic Spring Batch?
Many MarkLogic users often have custom batch processing jobs they want to execute but do not have a repeatable framework to use.  For example, a program must extract data from a relational database, transform the data, load it directly into MarkLogic, and must do all this without human intervention.  Sometimes this often gets coded up as mulitiple programs.  Or, let's say you want to request data from another system's REST API and load that data into MarkLogic.  More often than not, custom batch processing jobs can lead to an effort that ends up being more work that initially anticipated.  What happens in the event of invalid data or can you restart a job at the last successful execution.  MarkLogic Spring Batch builds on top of the proven Spring Batch framework and enhances it with several additional features.

# Features
* Ability to execute any job from a command line interface via the [Jobs]() program
* Perform common tasks related to a MarkLogic Batch Processing job via custom ItemReader, ItemProcesor, ItemWriter, and tasklet classes (i.e. Writing documents to MarkLogic)
* Execute one of many generic MarkLogic batch processing jobs for importing, transforming, and exporting data in a MarkLogic database
* Persist the metadata of any JobExecution with a MarkLogic [JobRepository](http://docs.spring.io/spring-batch/trunk/reference/html/domain.html#domainJobRepository)
* Mitigate the risk of the Spring Batch learning curve by providing several examples of creating your own custom batch processing job
* Leverage _all_ the features of Spring Batch in all your batch processing jobs 

# Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

## Prerequisities

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

_NOTE: There is an [open issue](https://github.com/sastafford/marklogic-spring-batch/issues/69) to address the multiple gradle.properties issues_

### Installing

Create the MarkLogic Job Repository application.  The appserver and database that is created will also serve as the target database for all testing. 

Assuming gradle is installed and on your machine.
```
gradle deployMarkLogicJobRepository
```

Assuming you are on a Unix terminal and gradle is not installed
```
./gradlew deployMarkLogicJobRepository
```
 
Assuming you are on a Windows terminal and gradle is not installed
```
gradlew.bat deployMarkLogicRepository
```

_For all gradle commands in this README, assuming that gradle is installed_

After this command finishes, an application server, content database, and modules database will be created on your MarkLogic instance.

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

### Jobs
To deploy the MarkLogic Jobs utility, execute the following gradle command

```
gradle :jobs:distZip
```

This will create the distribution archive file under ./jobs/build/distribution/jobs.zip

### MarkLogic Spring Batch Libraries
To deploy the marklogic-spring-batch core and jobs library to your local maven repository, first, increment the relevant version number in the gradle.properties file.

Publish artifacts to local maven repository

```
gradle publishToMavenLocal
```

Publish to bintray (authoritative personnel only)

```
gradle bintrayUpload
```


## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Scott A. Stafford** - [sastafford](https://github.com/sastafford)
* **Rob Rudin** - [rjrudin](https://github.com/rjrudin)
* **Venu Iyengar** - [venuiyengar](https://github.com/iyengar)
* **Sanju Thomas** - [sanjuthomas](https://github.com/sanjuthomas)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
