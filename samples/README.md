# MarkLogic Spring Batch Samples Project

The samples project demonstrates a process to build batch processing application using MarkLogic Spring Batch and Gradle.

Spring Batch provides several ways of [running a job](http://docs.spring.io/spring-batch/trunk/reference/html/configureJob.html#runningAJob).  This sample will concentrate on launching a job via the commmand line.

# How do I install the Samples application?

The samples application is built using the the [Gradle Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html).  The Gradle application plugin is a simple way of building a deployable command line based application.  Executing the following gradle task will install the samples application.   

    gradlew :samples:installDist

The installation package can be found under the ./build/install/samples.  Two start scripts, one for Windows and one for Unix, will be created and all the dependent runtime libraries will be packaged.  At this point, you can then execute the start script and pass the required and any optional parameters defined by your job. 
 
# How do I execute the Samples Application?

Spring Batch applications are launched via an interface called a [JobLauncher](http://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/launch/JobLauncher.html).  Through the implementation of this interface, jobs are launched.  There are two methods that the samples job can be launched.  

## CommandLineJobLauncher
    
Spring Batch provides a [CommandLineJobRunner](http://docs.spring.io/spring-batch/apidocs/org/springframework/batch/core/launch/support/CommandLineJobRunner.html) that is a basic launcher for starting jobs from the command line.  The default main class for the samples application is the CommandLineJobRunner.  

The arguments to this class can be provided on the command line (separated by spaces), or through stdin (separated by new line). They are as follows:

    jobPath <options> jobIdentifier (jobParameters)*

The command line options are as follows

 * jobPath: the XML or Java Config Spring application context containing a Job
 * -restart: (optional) to restart the last failed execution
 * -stop: (optional) to stop a running execution
 * -abandon: (optional) to abandon a stopped execution
 * -next: (optional) to start the next in a sequence according to the JobParametersIncrementer in the Job
 * jobIdentifier: the name of the job or the id of a job execution (for -stop, -abandon or -restart).
 * jobParameters: 0 to many parameters that will be used to launch a job specified in the form of key=value pairs.

For the samples application, the _jobPath_ parameter points to the Spring configuration that wires up your application.  For this example, the [JobsConfig](./src/main/java/com/marklogic/spring/batch/batch/samples/JobsConfig.java) provides the application context.    

### Examples

    samples.bat com.marklogic.spring.batch.samples.JobsConfig deleteDocumentsJob output_collections=monster

    samples.bat com.marklogic.spring.batch.samples.JobsConfig importDocumentsFromDirectory input_file_path=./samples/src/test/resources/data/*.xml input_file_pattern="(elmo|grover).xml" document_type=xml output_collections=sample

## MarkLogic Spring Batch Main Job Launcher

Included in the core project is a [main program](../core/src/main/java/spring/batch/Main.java) that also provides a command line job launcher.  Each MarkLogic Spring Batch execution application (based on the Main class) expects a few command line parameters.  Custom parameters can be defined in the JobConfiguration. 

  * config - The class name of your Job configuration
  * host - MarkLogic host
  * port - MarkLogic application port
  * username - MarkLogic user name
  * password - MarkLogic password
  * jrHost (optional) - the Job Repository MarkLogic host
  * jrPort (optional) - the Job Repository MarkLogic port
  * jrUsername (optional) - the Job Repository MarkLogic username
  * jrPassword (optional) - the Job Repository MarkLogic password
                                                                                                            
### Example

     ./samples --config com.marklogic.spring.batch.job.JobName --host localhost --port 8010 --username admin --password admin --customParam1 xyz --customParamX abc

# How do I build a MSB Application?

## Dependencies
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

## Create your Spring Batch Job 
The next step is to create your Spring Batch [job configuration](http://docs.spring.io/spring-batch/reference/html/configureJob.html).  The goal is to assemble the step, steps, or tasklets needed to execute a Job.  Each step requires an ItemReader, ItemProcessor, and ItemWriter.  Spring Batch offers many out of the box classes for these ItemReader/Processor/Writers and they should be leveraged first before reinventing the wheel.  MarkLogic Spring Batch offers custom ItemReader/Processor/Writer and tasklets that can be used in the [Infrastructure](../infrastructure/README.md) project.  

## Test your job
Create a test class by subclassing the [AbstractJobTest](https://github.com/sastafford/marklogic-spring-batch/blob/dev/test/src/main/java/com/marklogic/spring/batch/test/AbstractJobTest.java).  

# How do I distribute my command line job utility?

The following gradle command will create a zip file under ./build/distributions that can be transferred and executed on any host.  
 
    gradle distZip
