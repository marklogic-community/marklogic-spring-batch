# MarkLogic Spring Batch Project

The goal of this project is to facilitate loading of data into MarkLogic via the Spring Batch Framework. 

## Configuration
See [application.properties](https://github.com/sastafford/marklogic-spring-batch/blob/master/src/main/resources/config/application.properties) for connection to MarkLogic.  

## Jobs
 * loadGeonamesJob - [Geonames](http://www.geonames.org) - Geonames is an example of a CSV file with a few lookup values
 * corbJob - Runs a basic corb job.  Executes uris module (/uris.xqy) and process module (/process.xqy).  Uris module should only return uris, no count needed.  See \marklogic-spring-batch\src\test\java\com\marklogic\client\spring\batch\corb\CorbTest.java
 
 