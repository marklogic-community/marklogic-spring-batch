![GitHub version](https://img.shields.io/github/tag/marklogic-community/marklogic-spring-batch.svg)

| Branch | Status |
| ------------- | ------------- |
| master | ![master](https://circleci.com/gh/sastafford/marklogic-spring-batch/tree/master.png?circle-token=e1b8b3198d3416fcb535509f2e7d600444ef153e)  |
| dev  | ![dev](https://circleci.com/gh/sastafford/marklogic-spring-batch/tree/dev.png?circle-token=e1b8b3198d3416fcb535509f2e7d600444ef153e)  |
| ml-8  | ![ml-8](https://circleci.com/gh/sastafford/marklogic-spring-batch/tree/ml-8.png?circle-token=e1b8b3198d3416fcb535509f2e7d600444ef153e)  |


# What is Spring Batch?

[Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) is an open source framework for batch processing based on the [Spring Framework](http://projects.spring.io/spring-framework/).

Figure 1 depicts a high-level diagram of a Spring Batch program.  The green APPLICATION box represents the batch processing program that gets executed by a user.  This is usually a command line based application but it could also be triggered by an external source like a user interface.  Spring Batch provides two components, CORE and INFRASTRUCTURE, that enables the boilerplate code for creating an APPLICATION.  
  
  * CORE - contains the core runtime classes necessary to launch and control a batch job
  * INFRASTRUCTURE - contains common readers and writers, and services which are used by APPLICATION and CORE
  
![Spring Batch Architecture](http://docs.spring.io/spring-batch/trunk/reference/html/images/spring-batch-layers.png.pagespeed.ce.sMqaNr3V1Z.png)

*Figure 1*

Figure 2 shows the key concepts that make up the domain language of Spring Batch.  The boxes in blue represent concepts from the CORE component and yellow from the INFRASTRUCTURE component.  A Job has one to many steps, which has exactly one ItemReader, ItemProcessor, and ItemWriter. A job needs to be launched (JobLauncher), and meta data about the currently running process needs to be stored (JobRepository).

![Spring Batch Stereotypes](http://docs.spring.io/spring-batch/trunk/reference/html/images/spring-batch-reference-model.png.pagespeed.ce.TrtTC751hI.png)

*Figure 2*

# Why use Spring Batch?

 * If you need to migrate data from a relational database, mainframe, or other external source to MarkLogic.
 * If you have a batch processing job that is outside the scope of other batch processing tools like MLCP or CORB 
 * If you find that you need 'boilerplate' code to provide failover capability,  performance, and extensibility for a custom batch processing job. 
 * If you are a Java shop running MarkLogic 8+

# What is MarkLogic Spring Batch?

MarkLogic Spring Batch (MSB) extends the CORE and INFRASTRUCTURE components of Spring Batch to make it easier to work with data and MarkLogic.  Sample code templates are included for developers getting started writing a batch processing APPLICATION.

## What are the main features of MarkLogic Spring Batch?

 * Extends the [INFRASTRUCTURE]() classes to facilitate reading, writing, and processing documents for MarkLogic.  
 * A sample [APPLICATION]() that provides a template program to create your own Spring Batch program
 * (Beta) MarkLogic implementation of a JobRepository

# How can I get started using MarkLogic Spring Batch?

Check out the [Getting Started Wiki](), review the sample application, and check out other batch processing applications using Spring Batch

 * [ml-migration-starter]() - Migrate data from a relational database into MarkLogic
 * [Hector]() - Ingest CSV files into MarkLogic
 * [Penny]() - Use Apache Natural Language Processing library to perform named entity recognition over documents

# What are the goals for the MarkLogic Spring Batch project?

  * Write batch processing solutions that are reliable, robust, and high performing
  * Reduce the amount of time it takes to build, operate, and maintain batch processing jobs with MarkLogic
  * Minimize complexity of batch processing applications

# How can I contribute to the project?

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute code to this project and the process for submitting pull requests to us.

# What license does MarkLogic Spring Batch use?

See the [LICENSE.md](LICENSE.md) file for details

----
1. Spring Batch Introduction: http://docs.spring.io/spring-batch/trunk/reference/html/spring-batch-intro.html