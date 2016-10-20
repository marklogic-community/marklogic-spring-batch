# SQL Example #1 - RowToDocs

This is an batch processing job example that migrates the tabular rows of a SQL Query, transforms them into JSON using the column names and inserts the data into MarkLogic.  
 
 The RDBMS data set is using the [Invoice data](invoices-sql-diagram.jpg) from the [HSQL relational database](http://www.hsqldb.org/).  

## Create RowToDocs Distribution Program
  1) gradle installDist 

This command uses the [gradle application plugin](https://docs.gradle.org/current/userguide/application_plugin.html) to create a packaged program distribution.  

## Create/View the Invoices Database
  1) gradle runManager (this command will open up the HSQL Database Manager)
  2) File -> Open Script -> sampledata_ddl.sql -> Execute SQL
  3) File -> Open Script -> sampledata_insert.sql -> Execute SQL
  4) File -> Open Script -> sampledata_select.sql -> Execute SQL (this step to verify data was inserted)
  5) File -> Exit

## Execute RowToJson 
1) Find the program that was installed under build/install/invoices/bin (runInvoices.bat for Windows users).  This can also be done via the gradle command: `gradle importInvoices`





