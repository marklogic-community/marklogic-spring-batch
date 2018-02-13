![GitHub version](https://badge.fury.io/gh/marklogic-community%2Fmarklogic-spring-batch.svg)

| Branch | Status |
| ------------- | ------------- |
| master | ![master](https://circleci.com/gh/sastafford/marklogic-spring-batch/tree/master.png?circle-token=e1b8b3198d3416fcb535509f2e7d600444ef153e)  |
| dev  | ![dev](https://circleci.com/gh/sastafford/marklogic-spring-batch/tree/dev.png?circle-token=e1b8b3198d3416fcb535509f2e7d600444ef153e)  |

# MarkLogic Spring Batch

The MarkLogic Spring Batch project is an extension of the CORE and INFRASTRUCTURE components of Spring Batch to make it easier to write batch processing programs using MarkLogic. 

Start with the [project home page](https://github.com/marklogic-community/marklogic-spring-batch/wiki) to get started. 

## Prerequisites

 * MarkLogic 8+
 * JDK 1.8+

## Installing the Test Environment

Open $PROJECT_ROOT/gradle.properties.  Review the mlHost, mlRestPort, mlJobRepoPort properties to confirm there are no conflicts

    gradlew mlDeploy

This command will set up your test database and application server.

## Running the tests

Review the host/port/user credentials properties for the following property files.

 * ./core/src/test/resources/job.properties
 * ./infrastructure/src/test/resources/job.properties
 * ./rdf/src/test/resources/job.properties
 * ./samples/src/test/resources/job.properties
 
 Run the following command to execute all project tests.  All tests should pass. 

     gradlew test

## Coding Style Tests

This project uses both checkstyle and PMD.

     gradlew check

## Deployment

The product of this project are jar files that are published to [bintray](https://dl.bintray.com/sastafford/maven/).  The following libraries are created. 

| Group | Artifact |
| ------|----------|
| com.marklogic | marklogic-spring-batch-core |
| com.marklogic | marklogic-spring-batch-test |
| com.marklogic | spring-batch-http |
| com.marklogic | spring-batch-rdbms |
| com.marklogic | spring-batch-file |

The gradle bintray plugin is used to publish to bintray.

     gradlew :core:bintrayUpload
     gradlew :file:bintrayUpload
     gradlew :test:bintrayUpload
     gradlew :http:bintrayUpload
     gradlew :rdbms:bintrayUpload

## How do I use these libraries?

### Gradle

```groovy
dependencies {
    compile 'com.marklogic:marklogic-spring-batch-core:1.+'
    testCompile 'com.marklogic:marklogic-spring-batch-test:1.+'
}
```


### Maven

```xml
<dependencies>
    <dependency>
        <groupId>com.marklogic</groupId>
        <artifactId>marklogic-spring-batch-core</artifactId>
        <version>1.5.0</version>
    </dependency>
</dependencies>
```


# How can I contribute to the project?

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute code to this project and the process for submitting pull requests to us.

# What license does MarkLogic Spring Batch use?

See the [LICENSE.md](LICENSE.md) file for details
