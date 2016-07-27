# Invoices Example #1

This is an batch processing job example that migrates [Invoice data](invoices-sql-diagram.jpg) from a [HSQL relational database](http://www.hsqldb.org/) into MarkLogic.  This is an example of reusing the [MigrateColumnMaps]() batch processing job to extracting data from a relational database into MarkLogic.  

## Create/View the Invoices Database
1) gradle runManager
2) File -> Open Script -> sampledata_ddl.sql -> Execute SQL
3) File -> Open Script -> sampledata_insert.sql -> Execute SQL
4) File -> Open Script -> sampledata_select.sql -> Execute SQL (this step to verify data was inserted)
5) File -> Exit

## Execute the Invoices Batch Processing Job program
0) Make sure to create the invoices database first.  See prior section.   
1) Verify MarkLogic host, port, username, password properties located in gradle.properties 
2) gradle installDist - This is a task type of the [gradle application plugin](https://docs.gradle.org/current/userguide/application_plugin.html).  It is responsible for creating a packaged up script to execute a program  
3) Execute the program located under build/install/invoices/bin (runInvoices.bat for Windows users).  This can also be done via the gradle command: `gradle importInvoices`



