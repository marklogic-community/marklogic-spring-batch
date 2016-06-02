Read Me for RDF Migrator
==============================
The RDFMigrator is a sample migrator to import the sample triples from "tigers.ttl" to MarkLogic
using RdfTripleItemReader and RdfTripleItemWriter
The default chunksize is set to 50 and there are 107 triples in the file "trigers.ttl"

As part of the main RDFMigrator; please supply the arguments as below "Sample"
-host 192.168.1.7 -port 8200 -username example-rest-writer -password x -database spring-batch-content -auth DIGEST 
-rootLocalName myTripleStore -inputFilePath tigers.ttl

The triple data will be ingested into "myTripleStore" collection graph;
The triple data will be removed if you re-run the RDFMigrator
