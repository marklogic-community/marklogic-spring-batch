# SQL Example #1 - Import Invoice Documents

This is an batch processing job example that migrates the tabular rows of a SQL Query, transforms them into either JSON or XML and inserts the data into MarkLogic.

The RDBMS data set is using the [Invoice data](invoices-sql-diagram.jpg) from the [HSQL relational database](http://www.hsqldb.org/).  

This job can take two tables and create child elements from the many relationship.  For this example, we will examine the
customer and invoice tables.  These two tables are joined via a one (customer) to 
many (invoice) relationship.  We have decided that the root element is going to be based on the customer
and many invoice children will be created.  By renaming the column names using a 
**[parent element]/[child element]** naming convention in your SQL query, this batch processing job
 will create the document accordingly.  

    SELECT customer.*, invoice.id as \"invoice/id\", invoice.total as \"invoice/total\" 
    FROM invoice LEFT JOIN customer on invoice.customerId = customer.id 
    ORDER BY customer.id

In this example, this would generate the following sample document. 

    <invoice>
      <ID>13</ID>
      <FIRSTNAME>Laura</FIRSTNAME>
      <LASTNAME>Ringer</LASTNAME>
      <STREET>38 College Av.</STREET>
      <CITY>New York</CITY>
      <invoice>
        <id>43</id>
        <total>3215</total>
      </invoice>
      <invoice>
        <id>30</id>
        <total>1376</total>
      </invoice>
    </invoice>

Please note that any child elements beyond the second level are not yet supported.  

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
  
For Unix based systems, execute build\install\rowToJson\bin\rowToDoc







