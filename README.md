# What is Spring Batch?

[Spring Batch](http://docs.spring.io/spring-batch/trunk/reference/html/) provides reusable functions that are essential in processing large volumes of records, including logging/tracing, transaction management, job processing statistics, job restart, skip, and resource management. It also provides more advanced technical services and features that will enable extremely high-volume and high performance batch jobs through optimization and partitioning techniques. Simple as well as complex, high-volume batch jobs can leverage the framework in a highly scalable manner to process significant volumes of information.

# What is MarkLogic Spring Batch?
The vision of the MarkLogic Spring Batch (MSB) project is to provide the **BEST** solution for building custom batch processing jobs for the MarkLogic platform.  There are three goals of the MarkLogic Spring Batch project.  

* To provide recipes for writing custom batch processing programs that utilize MarkLogic.  Most data migration processes are very specific to one or more data sources.  You could use a generic batch processing job like loading files from a directory but typically a custom job is needed.  MarkLogic Spring Batch allows you to focus on the important business logic challenges and less on the operations.  
* To enhance the core [Spring Batch](http://docs.spring.io/spring-batch/apidocs/) framework that makes it easy to create batch processing programs using MarkLogic.  This represents the fusion of the MarkLogic Java Client API and the Spring Batch core libraries.  
* To create a library of common batch processing jobs that are executed against a MarkLogic database.  

# Why use MarkLogic Spring Batch?
Many MarkLogic users often have custom batch processing jobs they want to execute but do not have a repeatable framework to use.  For example, a program must extract data from a relational database, transform the data, load it directly into MarkLogic, and must do all this without human intervention.  Sometimes this often gets coded up as mulitiple programs.  Or, let's say you want to request data from another system's REST API and load that data into MarkLogic.  More often than not, custom batch processing jobs can lead to an effort that ends up being more work that initially anticipated.  What happens in the event of invalid data or can you restart a job at the last successful execution.  MarkLogic Spring Batch builds on top of the proven Spring Batch framework and enhances it with several additional features.

## Features
* Ability to execute any job from a command line interface via the [Jobs]() program
* Perform common tasks related to a MarkLogic Batch Processing job via custom ItemReader, ItemProcesor, ItemWriter, and tasklet classes (i.e. Writing documents to MarkLogic)
* Execute one of many generic MarkLogic batch processing jobs for importing, transforming, and exporting data in a MarkLogic database
* Persist the metadata of any JobExecution with a MarkLogic [JobRepository](http://docs.spring.io/spring-batch/trunk/reference/html/domain.html#domainJobRepository)
* Mitigate the risk of the Spring Batch learning curve by providing several examples of creating your own custom batch processing job
* Leverage _all_ the features of Spring Batch in all your batch processing jobs 

# How do I build a custom MarkLogic batch processing job? 

Refer to the [Wiki]() or the [FAQ]() for questions that you may come across.  

OK, you need to build a custom MarkLogic batch processing job. The first step is to create a build.gradle file and it needs to refer to the MarkLogic Spring Batch Jar files.  The following code snippet will get you started.    

```
plugins {
    id "com.marklogic.ml-gradle" version "2.+"
    id "java"
}

repositories {
    jcenter()
    mavenLocal()
    maven {url "https://dl.bintray.com/sastafford/maven/"}
}

dependencies {
    compile "com.marklogic:marklogic-spring-batch:0.+"
    runtime "com.marklogic:marklogic-spring-batch-jobs:0.+"
}
```

The next step is to create your Spring Batch [job configuration](http://docs.spring.io/spring-batch/reference/html/configureJob.html).  The procedure for putting together job configuration follows the standard Spring Batch way of creating jobs.  But we have provided [several examples of custom Spring Batch processing jobs for MarkLogic](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples).   

The recommended next step is to write a job test.  **TBD**  

Once your JobConfig is written and verified then the final step is to execute your program via the Jobs utility.  The Jobs utility (jobs.zip) can be downloaded from the [MSB releases page](https://github.com/sastafford/marklogic-spring-batch/releases).  Unzip the jobs.zip file onto your machine and then execute the scripts under the bin/ directory.     

```
./jobs --config com.marklogic.spring.batch.job.JobNameConfig.class --host localhost --port 8010 --username admin --password admin --customParam1 xyz --customParamX abc
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
