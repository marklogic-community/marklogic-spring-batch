package com.marklogic.spring.batch.rdf;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.spring.batch.AbstractSpringBatchTest;
import com.marklogic.spring.batch.item.RdfTripleItemReader;
import com.marklogic.spring.batch.item.RdfTripleItemWriter;

/**
 *	Handles triples data from a file and data is inserted using jena libraries into MarkLogic
 * 
 * This test class verifies the following:
 * <ol>
 * <li>Read triple rows using RDFTripleItemReader</li>
 * <li>Writes triples into MarkLogic using RDFTripleItemWriter</li>
 * <li>Uses chunk size of one (1) for batching reads and writes</li>
 * </ol>
 */
public class ReadAndWriteTripleTest extends AbstractSpringBatchTest {

    private RdfTripleItemReader<Map<String, Object>> reader;
	private RdfTripleItemWriter rdfWriter;
	private DatabaseClient client;

    @Before
    public void setup() {
    	reader = new RdfTripleItemReader<Map<String, Object>>();
    	reader.setFileName("triple/test1.ttl");	
    	//reader.setFileName("triple/tigers.ttl");
		client = getClient();
		rdfWriter = new RdfTripleItemWriter(client, "myTestGraph");		
    }

    @Test
    public void writeTriples() {
        readAndWriteTriples();
        assertTrue(1 == rdfWriter.getTripleCount());
    }
    @After
    public void teardown() {
        if (client != null) {
            //client.release();
        }
    }
    private void readAndWriteTriples() {	
        launchJobWithStep(stepBuilderFactory.get("testStep").<Map<String, Object>, Map<String, Object>> chunk(1)
                .reader(reader).writer(rdfWriter).build());
    }

}
