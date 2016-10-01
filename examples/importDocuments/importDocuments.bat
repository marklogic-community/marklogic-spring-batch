build\install\importDocuments\bin\importDocuments.bat ^
  --host localhost --port 8200 --username admin --password admin ^
  --config com.marklogic.spring.batch.config.ImportDocumentsFromDirectoryConfig ^
  --input_file_path src/test/resources/data/
  --jrHost oscar --jrPort 8200 --jrUsername admin --jrPassword admin