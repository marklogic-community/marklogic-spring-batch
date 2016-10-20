build\install\rowToJson\bin\sql.bat ^
  --host oscar --port 8200 --username admin --password admin ^
  --config com.marklogic.spring.batch.config.MigrateColumnMapsConfig ^
  --jdbc_driver org.hsqldb.jdbc.JDBCDriver ^
  --jdbc_url jdbc:hsqldb:file:data\\invoices ^
  --sql "SELECT * FROM invoice LEFT JOIN customer on invoice.customerId=customer.id LEFT JOIN item on invoice.id=item.invoiceId LEFT JOIN product on product.id=item.productId ORDER BY invoice.id asc" ^
  --jdbc_username sa ^
  --format json ^
  --root_local_name invoice ^
  --collections invoice
  