# What should I know before I get started?

Please read our [Vision Statement](https://github.com/sastafford/marklogic-spring-batch/wiki).  The reason for each task should ultimately come back to this statement. 

Each contributor should be knowledgeable and proficient at the following projects and technologies. 
* [Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/)
* [Spring Framework](https://projects.spring.io/spring-framework/)
* [Gradle](http://gradle.org/) 
  * [Java Plugin](https://docs.gradle.org/current/userguide/java_plugin.html)
  * [MarkLogic Gradle Plugin](http://developer.marklogic.com/code/ml-gradle)
* [MarkLogic Java Client API](http://developer.marklogic.com/products/java)
* [MarkLogic Java Client Util](https://github.com/rjrudin/ml-javaclient-util)
* [MarkLogic JUnit Library](https://github.com/rjrudin/ml-junit)
* [MarkLogic App Deployer](https://github.com/rjrudin/ml-app-deployer)
* [Git](http://git-scm.com/doc)

# How should I set up my development environment? 

## What software do I need?

* [MarkLogic 8+](http://developer.marklogic.com/products)
* [Java Development Kit 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Recommended: [Gradle 2.+](http://gradle.org/gradle-download/)

You will need to first install MarkLogic if you haven't already.  It is recommended to use a virtual machine or remote server to run MarkLogic to partition our resources (MarkLogic likes to use a lot of memory).  

## What code baseline should I use?
We use a [fork-and-Branch Git workflow](http://blog.scottlowe.org/2015/01/27/using-fork-branch-git-workflow/).  The dev branch contains the latest and greatest code, while master represents the latest published version.  **Always branch from DEV**.
  
Review the [jobs/gradle.properties](https://github.com/sastafford/marklogic-spring-batch/blob/master/core/gradle.properties) file to make sure that your host, port, username, and password are correct for your environment.  Do not check your local version of gradle.properties.  

## Should I use an IDE? 

Yes, it is recommended to use a Java friendly IDE to make one's life easier.  Of course, you can use a simple text editor if that is your preference.  The lead authors have a recent preference for [IntelliJ](https://www.jetbrains.com/idea/).  We also use Gradle for our swiss army knife of building, testing, deploying, etc.  One of the cool features of Gradle is the ability to create the project files for two popular IDE's, IntelliJ and Eclipse.    

```
./gradlew idea
```

```
./gradlew eclipse
```

The gradlew (or gradlew.bat on Windows) command is a Java program called the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) which allows you to run a gradle command without installing Gradle on your machine.  If you plan to contribute (or do any type of Java development) it would be worth while to go ahead and install Gradle on your dev machine.  

## How do I verify things are set up correctly?  
Once you have cloned the project locally execute the test cases.  Gradle will handle downloading any dependencies that you need for this effort.  

```
gradlew testAll
```

It is our policy that the dev branch tests always are 100% passing.  If all tests run to completion and 100% passing then you are ready to start coding.    

# What should I work on?

If you are new to the project the best way to contribute is to add a Job to the job library.  Choose of the [job issues](https://github.com/sastafford/marklogic-spring-batch/labels/job) to get started on an assignment.  If you have an example of a unique batch processing job then it would be a good idea to add it to the examples project.    

# What is the structure for the project?

There are three sub-projects for MarkLogic Spring Batch.  Most contributions will be in the examples and jobs projects where the core work would be for those intimately familiar with the Spring Batch code baseline.  

* [examples](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples) are the recipes for creating your own batch processing jobs (i.e. migrating from a RDBMS).  Developers should use these as templates and guidelines for writing their own MarkLogic batch processing jobs. 
* [jobs](https://github.com/sastafford/marklogic-spring-batch/tree/master/jobs) are a library of common MarkLogic batch processing jobs to import, export, and transform.  
* [core](https://github.com/sastafford/marklogic-spring-batch/tree/master/core) contains all enhancements on the Spring Batch framework.  This includes the jobs utility, the MarkLogic Job Repository, and any custom ItemReaders/Processors/Writers and tasklets.  

# How do I test? 

The quality of this project is based on the tests that exercise the code.  Every issue worked should be accompanied by one or more tests that test the primary use case and several edge cases.  Code that is submitted without any accompanied tests will not be accepted.  Before submitting a pull request, run the test suite and confirm 100% test passing with the following gradle command.  

```
./gradlew test
```

# How do I deploy the MarkLogic Spring Batch artifacts?

## MarkLogic Spring Batch Libraries
To deploy the marklogic-spring-batch core and jobs library to your local maven repository, first, increment the relevant version number in the gradle.properties file.

It is advised to first publish artifacts to the local maven repository and run local tests before publishing out to [bintray](https://bintray.com/)

```
gradle publishToMavenLocal
```

Once you have verified your version then run the following command to publish to bintray (authoritative personnel only)

```
gradle bintrayUpload
```

## MSB Command Line Utility
To deploy the MarkLogic Spring Batch command line program, execute the following gradle command

```
gradle distZip
```

This will create the distribution archive file under ./jobs/build/distribution/jobs.zip.  This can then be uploaded to the distribution website.  

# What is the best way to ask a question?  
Open an issue and label it as a Question.  Please don't use email.  This is the best way to ask once and answer for all.  If you are a MarkLogic employee, I recommend that you subscribe to the java-sig email newsgroup.  

# How do I report bugs?  
Bugs are tracked as [GitHub issues](https://guides.github.com/features/issues/). Create an issue, label it as a bug.

# How do I submit my changes?  
Once you are assigned an issue, please utilize pull requests to submit your changes.  Each pull request will be reviewed before being merged into the master branch.  

# What should I put in my Git commit messages?
[Seven rules of a great git commit message](http://chris.beams.io/posts/git-commit/)

1. Separate subject from body with a blank line
1. Limit the subject line to 50 characters
1. Capitalize the subject line
1. Do not end the subject line with a period
1. Use the imperative mood in the subject line
1. Wrap the body at 72 characters
1. Use the body to explain what and why vs. how

