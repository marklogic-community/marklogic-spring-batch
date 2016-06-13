# What is Spring Batch?

Many applications within the enterprise domain require bulk processing to perform business operations in mission critical environments. These business operations include automated, complex processing of large volumes of information that is most efficiently processed without user interaction. These operations typically include time based events (e.g. month-end calculations, notices or correspondence), periodic application of complex business rules processed repetitively across very large data sets (e.g. Insurance benefit determination or rate adjustments), or the integration of information that is received from internal and external systems that typically requires formatting, validation and processing in a transactional manner into the system of record. Batch processing is used to process billions of transactions every day for enterprises.<sup>1</sup>

[Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) provides reusable functions that are essential in processing large volumes of records, including logging/tracing, transaction management, job processing statistics, job restart, skip, and resource management. It also provides more advanced technical services and features that will enable extremely high-volume and high performance batch jobs through optimization and partitioning techniques. Simple as well as complex, high-volume batch jobs can leverage the framework in a highly scalable manner to process significant volumes of information.

# What is MarkLogic Spring Batch?
The vision of the MarkLogic Spring Batch (MSB) project is to provide a comprehensive and robust solution for building custom batch processing jobs for the MarkLogic platform.    

There are three goals of the MarkLogic Spring Batch project.  

* Develop batch processing solutions that are reliable, robust, and high performing
* Reduce the amount of time it takes to build, operate, and maintain batch processing jobs with MarkLogic
* Minimize the complexity of batch processing applications and allow developers to focus on application code

# Why use MarkLogic Spring Batch?
Have you ever written a long running batch processing job, like migrating data from a relational database, and ask yourself "Someone should invent a tool that automates this work?".  Have you ever written three separate programs that must be individually started to accomplish a batch transform?  Have you ever wondered why that one batch process failed after running for 8 hours and had no idea why?  If these are the types of technical challenges that keep you up at night then MarkLogic Spring Batch is your sleep aid.  

MarkLogic Spring Batch is the fusion of the Spring Batch processing framework and the MarkLogic Java Client API to makes it easier to write custom batch processing jobs.  All the details of reliability, performance, logging, reading/writing data to MarkLogic, and standard patterns are provided by MarkLogic Spring Batch.  This allows developers to focus in on writing application code and not worring about bathc processing mechanics.    

## Features
* Ability to execute any job from a command line interface via the [Jobs](https://github.com/sastafford/marklogic-spring-batch/wiki/Jobs-Utility) utility.
* Perform common tasks related to a MarkLogic Batch Processing job via custom ItemReader, ItemProcesor, ItemWriter, and tasklet classes (i.e. Writing documents to MarkLogic)
* Execute one of many generic MarkLogic batch processing jobs for importing, transforming, and exporting data in a MarkLogic database
* Persist the metadata of any JobExecution with a MarkLogic [JobRepository](http://docs.spring.io/spring-batch/trunk/reference/html/domain.html#domainJobRepository)
* Mitigate the risk of the Spring Batch learning curve by providing several examples of creating your own custom batch processing job
* Leverage _all_ the features of Spring Batch in all your batch processing jobs 

# How do I build a custom MarkLogic batch processing job? 

Refer to the [Wiki](https://github.com/sastafford/marklogic-spring-batch/wiki) or the [FAQ](https://github.com/sastafford/marklogic-spring-batch/wiki/Frequently-Asked-Questions) for questions that you may come across.  

OK, you need to build a custom MarkLogic batch processing job. The first step is to create a build.gradle file and it needs to refer to the MarkLogic Spring Batch Jar files.  The following code snippet will get you started.    

```
plugins {
    id "java"
}

repositories {
    jcenter()
}

dependencies {
    compile "com.marklogic:marklogic-spring-batch:0.4.0"
    runtime "com.marklogic:marklogic-spring-batch-jobs:0.4.0"
}
```

The next step is to create your Spring Batch [job configuration](http://docs.spring.io/spring-batch/reference/html/configureJob.html).  The goal is to assemble the step, steps, or tasklets needed to execute a Job.  The procedure for putting together job configuration follows the standard Spring Batch way of creating jobs.  Each step requires an ItemReader, ItemProcessor, and ItemWriter.  Spring Batch offers many out of the box classes for these ItemReader/Processor/Writers and they should be leveraged first before reinventing the wheel.  MarkLogic Spring Batch offers custom ItemReader/Processor/Writer and tasklets that can also be used.  But we have provided [several examples of custom Spring Batch processing jobs for MarkLogic](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples).   

# How do I test my job?
Subclass the AbstractJobTest - **TBD** 

# How do I execute my job? 

## The Jobs Utility

The first option is to execute a job via the [Jobs utility](https://github.com/sastafford/marklogic-spring-batch/wiki/Jobs-Utility).  The Jobs utility (jobs.zip) can be downloaded from the [MSB releases page](https://github.com/sastafford/marklogic-spring-batch/releases).  Unzip the jobs.zip file onto your machine and then execute the scripts under the bin/ directory.  This is recommended when you are deploying into operations.       

```
./jobs --config com.marklogic.spring.batch.job.JobNameConfig.class --host localhost --port 8010 --username admin --password admin --customParam1 xyz --customParamX abc
```

## Create a Gradle JavaExec task

The second option is to create a gradle JavaExec task that would execute your job via a gradle command.  Here is an example gradle task taken from the [mysql-sakila example](https://github.com/sastafford/marklogic-spring-batch/blob/master/examples/mysql-sakila/build.gradle).  

```
task migrateActors(type: JavaExec) {
    classpath = configurations.runtime
    main = "com.marklogic.spring.batch.Main"
    args = [
        "--config", "com.marklogic.spring.batch.config.MigrateColumnMapsConfig",
        "--host", mlHost,
        "--port", mlRestPort,
        "--username", mlUsername,
        "--password", mlPassword,
        "--jdbcDriver", "com.mysql.jdbc.Driver",
        "--jdbcUrl", "jdbc:mysql://localhost:3306/sakila",
        "--jdbcUsername", "root",
        "--jdbcPassword", "admin",
        "--sql", actorsSql,
        "--rootLocalName", "actor"
    ]
}
``` 

Once this task is in your build.gradle file then you would execute with the following command
```
gradle migrateActors
```

# How can I use the MarkLogic Job Repository?
If you want to use a MarkLogic Job Repository then you first need to install a new application onto your MarkLogic database.  A MarkLogic Job Repository can be installed via the jobs utility.  

```
./jobs deployMarkLogicJobRepository --jrHost localhost --jrPort 8011 --jrUsername admin --jrPassword admin
```

If you ever need to undeploy the JobRepository then you can issue the following command.

```
./jobs undeployMarkLogicJobRepository --jrHost localhost --jrPort 8011 --jrUsername admin --jrPassword admin
```

Now when you execute your job, then add the additional parameters for the MarkLogic Job Repository.  All JobExecution metadata is now logged to the MarkLogic JobRepository.  

```
./jobs --config com.marklogic.spring.batch.job.JobNameConfig.class --host localhost --port 8010 --username admin --password admin --customParam1 xyz --customParamX abc --jrHost localhost --jrPort 8011 --jrUsername admin --jrPassword admin
```

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