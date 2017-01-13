# MarkLogic Spring Batch Samples

The samples project is intended to provide an example of how a batch processing application can be built using MarkLogic Spring Batch.


  Using the command line job runner
  samples.bat com.marklogic.spring.batch.samples.JobsConfig deleteDocumentsJob output_collections=monster


* [BaseJob]()
* [Geonames]()
* [Entity Enrichment](enrichment.md)



#### Build a Batch Processing Application Quickly

A common use case for executing a batch processing application is the ability to execute a command line application with a variable number of custom parameters.  A [Main program](https://github.com/sastafford/marklogic-spring-batch/blob/master/core/src/main/java/com/marklogic/spring/batch/Main.java) is provided to execute that command line program.  

# How do I build a MarkLogic batch processing application? 

The [examples directory](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples) provides different examples of MarkLogic batch processing applications.  Each example is setup to be deployed as a command line utility.  The [base job example](https://github.com/sastafford/marklogic-spring-batch/tree/master/examples/base) is a bare bones Spring configuration template for creating a custom ingest batch processing job.

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
    compile "com.marklogic:marklogic-spring-batch-core:0.7.4"
    testCompile "com.marklogic:marklogic-spring-batch-test:0.7.4"
}
```

## Create your Spring Batch Job Configuration
The next step is to create your Spring Batch [job configuration](http://docs.spring.io/spring-batch/reference/html/configureJob.html).  The goal is to assemble the step, steps, or tasklets needed to execute a Job.  Each step requires an ItemReader, ItemProcessor, and ItemWriter.  Spring Batch offers many out of the box classes for these ItemReader/Processor/Writers and they should be leveraged first before reinventing the wheel.  MarkLogic Spring Batch offers custom ItemReader/Processor/Writer and tasklets that can also be used.  Take a look at the [JobConfiguration for the base example](https://github.com/sastafford/marklogic-spring-batch/blob/dev/examples/base/src/main/java/example/YourJobConfig.java).

## Test your job
Create a test class by subclassing the [AbstractJobTest](https://github.com/sastafford/marklogic-spring-batch/blob/dev/test/src/main/java/com/marklogic/spring/batch/test/AbstractJobTest.java).  Check out the [test class](https://github.com/sastafford/marklogic-spring-batch/blob/dev/examples/base/src/test/java/example/YourJobTest.java) from the base example. 

# How do I execute my job? 
An easy way to execute your job is to execute it as a command line utility similar to MarkLogic Content Pump or CORB.  An easy way to deploy the job is via the [Gradle Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html).  Once you have your JobConfiguration created and have verified that it works, then execute the following gradle command to build an executable.

    gradle installDist

This command will create two start scripts, one for Windows and one for Unix, and will package up all the libraries.  The installation package can be found under the ./build/install/<artifactId>.   directory where artifactId is a variable of the same name defined in your build.gradle or gradle.properties file.  Once this exists, you can then execute the start script and pass the required and any optional parameters defined by your job.  See either the [importInvoices task from the rdbms_2 build.gradle file](https://github.com/sastafford/marklogic-spring-batch/blob/master/examples/rdbms_2/build.gradle) or the manually created [runInovices.bat](https://github.com/sastafford/marklogic-spring-batch/blob/master/examples/rdbms_2/runInvoices.bat) file.  The runInvoices.bat assumes that the gradle installDist task has already been executed.   
    
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
./<artifactId_script_name> --config com.marklogic.spring.batch.job.JobNameConfig --host localhost --port 8010 --username admin --password admin --customParam1 xyz --customParamX abc
```

# How do I distribute my command line job utility?

The following gradle command will create a zip file under ./build/distributions that can be transferred and executed on any host.  
 
    gradle distZip

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
