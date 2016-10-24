build\install\rowToDoc\bin\rowToDoc.bat ^
  --host oscar --port 8200 --username admin --password admin ^
  --config com.marklogic.spring.batch.config.RowToDocConfig ^
  --jdbc_driver org.hsqldb.jdbc.JDBCDriver ^
  --jdbc_url jdbc:hsqldb:file:data\\invoices ^
  --sql "SELECT customer.*, invoice.id as \"invoice/id\", invoice.total as \"invoice/total\" FROM invoice LEFT JOIN customer on invoice.customerId = customer.id ORDER BY customer.id" ^
  --jdbc_username sa ^
  --format json ^
  --root_local_name invoice ^
  --collections invoice
