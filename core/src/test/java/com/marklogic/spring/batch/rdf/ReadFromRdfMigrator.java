package com.marklogic.spring.batch.rdf;

import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ReaderNotOpenException;

import com.marklogic.migration.rdf.RdfMigrator;
import com.marklogic.spring.batch.AbstractSpringBatchTest;

/**
 * RdfMigrator end-to-end test
 */
public class ReadFromRdfMigrator extends AbstractSpringBatchTest {

    private RdfMigrator rdfMigrator;

    @Before
    public void setup() {
        rdfMigrator = new RdfMigrator(getClient());
    }

    /**
     * This validates the RdfMigrator
     */
    @Test
    public void myMigrateTest() {
		rdfMigrator.setChunkSize(25);
		rdfMigrator.migrate("triple/tigers.ttl", "MyRDFMigrateGraph");
    }
    /**
     * This validates the RdfMigrator - File not found
     */
    @Test
    public void myMigrateFileNotFoundTest() {
		rdfMigrator.setChunkSize(100);
		try {
			rdfMigrator.migrate("db/tigers.ttl", "MyRDFMigrateFailGraph");
		}
		catch (Exception ex)
		{
			//System.out.println(ex.getMessage());
			assertEquals("Reader must be open before it can be read.", ex.getMessage());
			//"Reader must be open before it can be read."
		}
    }    
}
