![GitHub version](https://badge.fury.io/gh/sastafford%2Fmarklogic-spring-batch.svg)

| Branch | Status |
| ------------- | ------------- |
| master | ![master](https://circleci.com/gh/sastafford/marklogic-spring-batch/tree/master.png?circle-token=e1b8b3198d3416fcb535509f2e7d600444ef153e)  |
| dev  | ![dev](https://circleci.com/gh/sastafford/marklogic-spring-batch/tree/dev.png?circle-token=e1b8b3198d3416fcb535509f2e7d600444ef153e)  |

# What is Spring Batch?

[Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) is an open source framework for batch processing. It is a lightweight, comprehensive solution designed to enable the development of robust batch applications, often found in modern enterprise systems.  Spring Batch is a sub-project of the Spring Framework.  It provides reusable functions that are essential in processing large volumes of records, including logging/tracing, transaction management, job processing statistics, job restart, skip, and resource management. It also provides more advanced technical services and features that will enable extremely high-volume and high performance batch jobs though optimization and partitioning techniques.

# What is MarkLogic Spring Batch?

The vision of the MarkLogic Spring Batch (MSB) project is to provide a comprehensive and robust solution for building custom batch processing jobs for the MarkLogic platform.  The MSB project extends the Spring Batch architecture to provide reusable classes and components to quickly build batch processing applications.
  
![Spring Batch Architecture](http://docs.spring.io/spring-batch/trunk/reference/html/images/spring-batch-layers.png.pagespeed.ce.sMqaNr3V1Z.png)

# What are the goals for the MarkLogic Spring Batch project?

  * Write batch processing solutions that are reliable, robust, and high performing
  * Reduce the amount of time it takes to build, operate, and maintain batch processing jobs with MarkLogic
  * Minimize complexity of batch processing applications

# Why use MarkLogic Spring Batch?

 * Have you written a 'boilerplate' code to build in failover capability,  performance, and extensibility for a custom batch processing job? 
 * Have you ever migrated data from a relational database to MarkLogic and wondered why someone has not invented a generic tool to do this work?
 * Have you ever orchestrated a multi-step batch process which requires any combination of a multi-step INGEST, TRANSFORM, or EXPORT process? 

There are common themes when writing batch processing jobs which get addressed with a framework like Spring Batch.  The motivation for using Spring is to outsource the boilerplate code and concentrate on business logic.  The idea of MarkLogic Spring Batch is to continue that theme where developers can concentrate on business logic and less on the common MarkLogic boilerplate activities.  

## What are the main features of MarkLogic Spring Batch?

### Provide a Sample Spring Batch Application

The top layer of the Spring Batch architecture is the application layer.  The application contains all batch jobs and custom code written by developers using Spring Batch. Look under the [samples](./samples/README.md) project for an example application.   

Here are some batch processing applications using Spring Batch

 * [rowToDoc](https://github.com/sastafford/rowToDoc)

### Extend the Spring Batch Core

The Batch Core contains the core runtime classes necessary to launch and control a batch job. It includes things such as a [JobLauncher](), [Job](), and [Step]() implementations. The MSB project extends several batch core classes intended for specific use for MarkLogic.  MSB core extensions can be found under the [core subproject](./core/README.md).  

### Extend the Spring Batch Infrastructure  

Both Application and Core are built on top of a common infrastructure.  The Spring Batch infrastructure contains common [readers](http://docs.spring.io/spring-batch/trunk/reference/html/listOfReadersAndWriters.html#itemReadersAppendix), [writers](http://docs.spring.io/spring-batch/trunk/reference/html/listOfReadersAndWriters.html#itemWritersAppendix), and services which are used both by application developers and the core framework itself.  MarkLogic Spring Batch extends the infrastructure base classes to facilitate reading, writing, and processing documents for MarkLogic.  These classes can be found under the [infrastructure subproject](./infrastructure/README.md).  

# How can I contribute to the project?

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute code to this project and the process for submitting pull requests to us.

# What does each version represent? 

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

# Who are the masterminds behind this project?

* **Scott A. Stafford** - [sastafford](https://github.com/sastafford)
* **Rob Rudin** - [rjrudin](https://github.com/rjrudin)
* **Venu Iyengar** - [venuiyengar](https://github.com/iyengar)
* **Sanju Thomas** - [sanjuthomas](https://github.com/sanjuthomas)

# What license does MarkLogic Spring Batch use?

See the [LICENSE.md](LICENSE.md) file for details

----
1. Spring Batch Introduction: http://docs.spring.io/spring-batch/trunk/reference/html/spring-batch-intro.html