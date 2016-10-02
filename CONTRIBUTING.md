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

# What software do I need?

* [MarkLogic 8+](http://developer.marklogic.com/products)
* [Java Development Kit 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Recommended: [Gradle 3.+](http://gradle.org/gradle-download/)

You will need to first install MarkLogic if you haven't already.  It is recommended to use a virtual machine or remote server to run MarkLogic to partition our resources (MarkLogic likes to use a lot of memory).  

# What code baseline should I use?
We use a [fork-and-Branch Git workflow](http://blog.scottlowe.org/2015/01/27/using-fork-branch-git-workflow/).  The dev branch contains the latest and greatest code, while master represents the latest published version.  **Always branch from DEV**.  If you issue a pull request, make sure to compare against the DEV branch.  

# Should I use an IDE? 
Yes, it is recommended to use a Java friendly IDE to make one's life easier.  Of course, you can use a simple text editor if that is your preference.  The lead authors have a recent preference for [IntelliJ](https://www.jetbrains.com/idea/).  We also use Gradle for our swiss army knife of building, testing, deploying, etc.  One of the cool features of Gradle is the ability to create the project files for two popular IDE's, IntelliJ and Eclipse.    

```
./gradlew idea
```

```
./gradlew eclipse
```

The gradlew (or gradlew.bat on Windows) command is a Java program called the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) which allows you to run a gradle command without installing Gradle on your machine.  If you plan to contribute (or do any type of Java development) it would be worth while to go ahead and install Gradle on your dev machine.  

# What is the structure for the project?

* [core](https://github.com/sastafford/marklogic-spring-batch/tree/master/core) - contains enhancements and helper classes to the Spring Batch framework.  This includes the following features
    * A job repository that uses MarkLogic to persist job metadata
    * Custom ItemReaders/Processors/Writers and tasklets that work with MarkLogic
    * Configuration annotations for using MarkLogic and Spring Batch together 
* [test](https://github.com/sastafford/marklogic-spring-batch/tree/master/test) - helper classes to assist in the testing of Spring Batch jobs written for MarkLogic.  
* [examples](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples) - A variety of recipes and examples to guide in the creation of your own MarkLogic batch processing jobs (i.e. migrating from a RDBMS) 

# How do I set up my environment?  

## Deploy your MarkLogic test database

Check the following properties file to see if there is any conflicts with your existing system.

    $PROJET_ROOT/core/gradle.properties

From the project root, run the following command.  This will create a test database that is configured as a MarkLogic Job Repository.  

    gradlew :core:mlDeploy

Run the automated unit tests from the project root

```
gradlew test
```

The policy is the dev branch tests are 100% passing.  If all tests run to completion and 100% passing then you are ready to start commiting code.    

# What should I work on?

If you are new to the project the best way to contribute is to add or improve an example to the directory of examlpes.  Choose of the [job issues](https://github.com/sastafford/marklogic-spring-batch/labels/example) to get started on an assignment.  

# How do I test? 

The quality of this project is based on the tests that exercise the code.  Every issue worked should be accompanied by one or more tests that test the primary use case and several edge cases.  Code that is submitted without any accompanied tests will not be accepted.  Before submitting a pull request, run the test suite and confirm 100% test passing with the following gradle command.  

# How do I deploy the MarkLogic Spring Batch artifacts?

## MarkLogic Spring Batch Libraries
To deploy the marklogic-spring-batch core and test library to your local maven repository, first, increment the relevant version number in the gradle.properties file.

It is advised to first publish artifacts to the local maven repository and run local tests before publishing out to [bintray](https://bintray.com/)

```
gradle publishToMavenLocal
```

Once you have verified your version then run the following command to publish to bintray (authoritative personnel only)

```
gradle bintrayUpload
```

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

