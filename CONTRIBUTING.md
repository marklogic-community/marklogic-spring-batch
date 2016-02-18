# Contribution Guidelines

Thank you for choosing to contribute to the MarkLogic Spring Batch (MSB) project.  

### Table of Contents
[What should I know before I get started?](#what-should-i-know-before-i-get-started)
  * [Vision Statement](#vision-statement)
  * [Roadmap](#roadmap)
  * [Background Information](#background-information)

[How Can I Contribute?](#how-can-i-contribute)
  * [Development Environment](#development-environment)
  * [Asking Questions](#asking-questions)
  * [Reporting Bugs](#reporting-bugs)
  * [Pull Requests](#pull-requests)

[Styleguides](#styleguides)
  * [Git Commit Messages](#git-commit-messages)
  * [Java Coding Guidelines](#java-coding-guidelines)

## What should I know before I get started?

### Vision Statement
Please read our [Vision Statement](https://github.com/sastafford/marklogic-spring-batch/wiki).  The reason for each task should ultimately come back to this statement. 

### Roadmap
Our [roadmap](https://github.com/sastafford/marklogic-spring-batch/wiki/Roadmap) is the 10k foot view of where we're going. 

### Background Information 
Each contributor should be knowledgeable and proficient at the following projects and technologies. 
* [Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/)
* [MarkLogic Java Client API](http://docs.marklogic.com/guide/java)
* [MarkLogic JUnit Helper Library](https://github.com/rjrudin/ml-junit)
* [Gradle](http://www.gradle.org)
  * [MarkLogic Plug-In](https://github.com/rjrudin/ml-gradle)
  * [Java Plugin](https://docs.gradle.org/current/userguide/java_plugin.html)
* [Git](http://git-scm.com/doc)
  * [Using the Fork-and-Branch Git Workflow](http://blog.scottlowe.org/2015/01/27/using-fork-branch-git-workflow/)

## How can I contribute?

### Development Environment
It is recommended that an IDE is used for making one's life easier.  This is a heavy Java project where you will need to run individual unit tests.  Most people choose to use Eclipse.  

If you have cloned this project and wish to set up an Eclipse project, please execute the following command to set up all the necessary Eclipse project metadata files.

````
gradlew eclipse
````

### Asking Questions
Please open an Issue and label it as a Question.  Please don't use email.  This is the best way to ask once and answer for all.  If you are a MarkLogic employee, I recommend that you subscribe to the java-sig email newsgroup.  

### Reporting Bugs
Bugs are tracked as [GitHub issues](https://guides.github.com/features/issues/). Create an issue, label it as a bug.

### Pull Requests
Once you are assigned an issue, please utilize pull requests to submit your changes.  Each pull request will be reviewed before being merged into the master branch.  

## Styleguides

### Git Commit Messages
[Seven rules of a great git commit message](http://chris.beams.io/posts/git-commit/)

1. Separate subject from body with a blank line
1. Limit the subject line to 50 characters
1. Capitalize the subject line
1. Do not end the subject line with a period
1. Use the imperative mood in the subject line
1. Wrap the body at 72 characters
1. Use the body to explain what and why vs. how

### Java Coding Guidelines
 * No Warnings
 * Use Javadocs
 * Each package should have a package-info.java







