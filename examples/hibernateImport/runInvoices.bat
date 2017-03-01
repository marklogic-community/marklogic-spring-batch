build\install\invoices\bin\invoices.bat ^
  --host oscar --port 8200 --username admin --password admin ^
  --config example.ExtractInvoiceDataToMarkLogicConfig ^
  --jdbc_driver org.hsqldb.jdbc.JDBCDriver ^
  --jdbc_url jdbc:hsqldb:file:E:\gitrepo\marklogic-spring-batch\examples\rdbms_2\data\invoices ^
  --jdbc_username sa