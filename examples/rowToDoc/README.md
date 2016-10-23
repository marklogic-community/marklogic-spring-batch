# SQL Example #1 - Import Invoice Documents

This is an batch processing job example that migrates the tabular rows of a SQL Query, transforms them into either JSON or XML and inserts the data into MarkLogic.  

The RDBMS data set is using the [Invoice data](invoices-sql-diagram.jpg) from the [HSQL relational database](http://www.hsqldb.org/).  

## Create RowToDoc Distribution Program
  1) gradle installDist 
  2) Verify the program that was installed under ./build/install/rowToDoc
  
This command uses the [gradle application plugin](https://docs.gradle.org/current/userguide/application_plugin.html) to create a packaged program distribution.  

## Create/View the Invoices Database
  1) gradle runManager (this command will open up the HSQL Database Manager)
  2) File -> Open Script -> sampledata_ddl.sql -> Execute SQL
  3) File -> Open Script -> sampledata_insert.sql -> Execute SQL
  4) File -> Open Script -> sampledata_select.sql -> Execute SQL (this step to verify data was inserted)
  5) File -> Exit

## Execute RowToDoc Script 
  1) Open rowToDoc.bat and check the host, port, username, and password parameters to make sure that they point to a valid MarkLogic application server
  2) Execute: rowToDoc.bat
  
For Unix based systems, execute build\install\rowToJson\bin\sql

The program can also be executed via a Gradle JavaExec task

    gradle importInvoices






