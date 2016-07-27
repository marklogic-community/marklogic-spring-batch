# Invoices Example

This is an batch processing job example that migrates [Invoice data](invoices-sql-diagram.jpg) from a [HSQL relational database](http://www.hsqldb.org/) into MarkLogic.  The relational database in this example is [HSQL DB].  

The ItemReader uses the [JdbcCursorItemReader](http://docs.spring.io/spring-batch/apidocs/org/springframework/batch/item/database/JdbcCursorItemReader.html) to pass in the RDBMS connection URL and the SELECT SQL query to run.  Each row of this [select query]() is then [mapped to an Invoice POJO]().   

The ItemProcessor is an anonymous class instantiated in the JobConfig.  Using JAXB, the POJO is marshalled into a [document DOM object]().  

Finally, the ItemWriter is the [DocumentItemWriter](https://github.com/sastafford/marklogic-spring-batch/blob/master/core/src/main/java/com/marklogic/spring/batch/item/DocumentItemWriter.java) which takes the Document DOM object and persists it into MarkLogic.  




## Create/View the Invoices Database
1) gradle runManager
2) File -> Open Script -> src/test/resources/db/sampledata_ddl.sql -> Execute SQL
3) File -> Open Script -> src/test/resources/db/sampledata_insert.sql -> Execute SQL
4) File -> Open Script -> src/test/resources/db/select.sql -> Execute SQL (this step to verify data was inserted)
5) File -> Exit

## Execute the Invoices Batch Processing Job
6) Verify properties located in gradle.properties 
1) gradle installDist
8) Verify data in QConsole


