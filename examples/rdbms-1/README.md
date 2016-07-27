# Invoices Example

This is an example of using the MigrateColumnMaps batch processing job to extracting data from a relational database into MarkLogic.  The relational database in this example is [HSQL DB](http://www.hsqldb.org/).  

## Invoices Database

The Invoices database is the sample database included with HSQL DB.  It is a simple database with 4 tables.  As noted in this [E/R diagram](invoices-sql-diagram.jpg), there is a one-many relationship and a many-many relationship example.  MarkLogic stores entities in a document form and for this example, we will be creating Invoice entities.  We want each document in MarkLogic to represent an entity.  From the E/R diagram we know that each invoice will have ONE customer and ONE to MANY products. 

## Steps
1) gradle runManager
2) File -> Open Script -> sampledata_ddl.sql -> Execute SQL
3) File -> Open Script -> sampledata_insert.sql -> Execute SQL
4) File -> Open Script -> sampledata_select.sql -> Execute SQL (this step to verify data was inserted)
5) File -> Exit
6) Verify properties located in gradle.properties 
7) gradle importInvoices
8) Verify data in QConsole


