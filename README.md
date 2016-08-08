# What is Spring Batch?

[Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) is an open source framework for batch processing. It is a lightweight, comprehensive solution designed to enable the development of robust batch applications, often found in modern enterprise systems.  Spring Batch is a sub-project of the Spring Framework.  It provides reusable functions that are essential in processing large volumes of records, including logging/tracing, transaction management, job processing statistics, job restart, skip, and resource management. It also provides more advanced technical services and features that will enable extremely high-volume and high performance batch jobs though optimization and partitioning techniques.

# What is MarkLogic Spring Batch?
MarkLogic Spring Batch is the fusion of the Spring Batch processing framework and the MarkLogic Java Client API.  The vision is to provide a comprehensive and robust solution for building custom batch processing jobs for the MarkLogic platform.    

# What are the goals for the MarkLogic Spring Batch project?

  * Write batch processing solutions that are reliable, robust, and high performing
  * Reduce the amount of time it takes to build, operate, and maintain batch processing jobs with MarkLogic
  * Minimize complexity of batch processing applications

# Why use MarkLogic Spring Batch?
Have you ever written a long running batch processing job, like migrating data from a relational database, and ask yourself "Someone should invent a tool that automates this work?".  Do you have to execute multiple custom programs and a Content Pump or CORB job to accomplish a batch transform?  Have you ever wondered why that one batch process failed after running for 8 hours and had no idea why?  If these are the types of technical challenges that keep you up at night then MarkLogic Spring Batch is your sleep aid.  

# What are the main features of MarkLogic Spring Batch?
* Provide custom ItemReaders, ItemProcessors, ItemWriters, and tasklets for writing custom MarkLogic batch processing jobs.
* Persist the metadata of any JobExecution with a MarkLogic [JobRepository](http://docs.spring.io/spring-batch/trunk/reference/html/domain.html#domainJobRepository)
* Execute one of many generic MarkLogic batch processing jobs for importing, transforming, and exporting data in a MarkLogic database
* Mitigate the risk of the Spring Batch learning curve by providing examples of creating your own custom batch processing job

# How do I build a custom MarkLogic batch processing job? 

The [examples directory](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples) provides examples of MarkLogic batch processing jobs.  Each example is setup to be deployed as a command line utility.  The [base job](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples/base) provides a template for creating a custom import batch processing job.    

## Import the MSB jars
Create a build.gradle file to import the MarkLogic Spring Batch (MSB) jars.  Use the [latest version](https://github.com/sastafford/marklogic-spring-batch/releases) of the MSB jars.  

```
plugins {
    id "java"
    id "application"
}

repositories {
    jcenter()
}

dependencies {
    compile "com.marklogic:marklogic-spring-batch-core:0.5.0"
    testComplile "com.marklogic:marklogic-spring-batch-test:0.5.0"
}
```

## Create your Spring Batch Job Configuration
The next step is to create your Spring Batch [job configuration](http://docs.spring.io/spring-batch/reference/html/configureJob.html).  The goal is to assemble the step, steps, or tasklets needed to execute a Job.  Each step requires an ItemReader, ItemProcessor, and ItemWriter.  Spring Batch offers many out of the box classes for these ItemReader/Processor/Writers and they should be leveraged first before reinventing the wheel.  MarkLogic Spring Batch offers custom ItemReader/Processor/Writer and tasklets that can also be used.  Take a look at the [JobConfiguration for the base example](https://github.com/sastafford/marklogic-spring-batch/blob/dev/examples/base/src/main/java/example/YourJobConfig.java).

## Test your job
Create a test class by subclassing the [AbstractJobTest](https://github.com/sastafford/marklogic-spring-batch/blob/dev/test/src/main/java/com/marklogic/spring/batch/test/AbstractJobTest.java).  Check out the [test class](https://github.com/sastafford/marklogic-spring-batch/blob/dev/examples/base/src/test/java/example/YourJobTest.java) from the base example. 

# How do I execute my job? 
An easy way to execute your job is to execute it as a command line utility similar to MarkLogic Content Pump or CORB.  An easy way to deploy the job is via the [Gradle Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html).  Once you have your JobConfiguration created and have verified that it works, then execute the following gradle command to build an executable.

    gradle distZip
    
This will create a zip file under ../install/distributions that can be transferred and executed on any host.  

Each MarkLogic Spring Batch execution application (based on the Main class) expects a few command line parameters.  Custom parameters can be defined in the JobConfiguration. 

  * config - The class name of your Job configuration
  * host - MarkLogic host
  * port - MarkLogic application port
  * username - MarkLogic user name
  * password - MarkLogic password
  * jrHost (optional) - the Job Repository MarkLogic host
  * jrPort (optional) - the Job Repository MarkLogic port
  * jrUsername (optional) - the Job Repository MarkLogic username
  * jrPassword (optional) - the Job Repository MarkLogic password

```
./jobExec --config com.marklogic.spring.batch.job.JobNameConfig.class --host localhost --port 8010 --username admin --password admin --customParam1 xyz --customParamX abc
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
        "--jdbc_driver", "com.mysql.jdbc.Driver",
        "--jdbc_url", "jdbc:mysql://localhost:3306/sakila",
        "--jdbc_username", "root",
        "--jdbc_password", "admin",
        "--sql", actorsSql,
        "--root_local_name", "actor"
    ]
}
``` 

Once this task is in your build.gradle file then you would execute with the following command
```
gradle migrateActors
```

# How can I use the MarkLogic Job Repository?
If you want to use a MarkLogic Job Repository then you first need to install a new application onto your MarkLogic database.  A MarkLogic Job Repository can be installed via the executable created with your Gradle application plugin.  

```
./jobs deployMarkLogicJobRepository --jr_host localhost --jr_port 8011 --jr_username admin --jr_password admin
```

If you ever need to undeploy the JobRepository then you can issue the following command.

```
./jobs undeployMarkLogicJobRepository --jr_host localhost --jr_port 8011 --jr_username admin --jr_password admin
```

Now when you execute your job, then add the additional parameters for the MarkLogic Job Repository.  All JobExecution metadata is now logged to the MarkLogic JobRepository.  

```
./jobs --config com.marklogic.spring.batch.job.JobNameConfig.class --host localhost --port 8010 --username admin --password admin --custom_param1 xyz --customParamX abc --jr_host localhost --jr_port 8011 --jr_username admin --jr_password admin
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