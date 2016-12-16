package com.marklogic.spring.batch.samples.geonames;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.test.AbstractJobTest;
import com.marklogic.spring.batch.test.SpringBatchNamespaceProvider;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.geonames.IngestGeonamesToMarkLogicJob.class})
public class IngestGeonamesToMarkLogicJobTest extends AbstractJobTest {

    public IngestGeonamesToMarkLogicJobTest() {
        setNamespaceProvider(new SpringBatchNamespaceProvider());
    }

    @Test
    public void ingestCitiesTest() {
        runJob(IngestGeonamesToMarkLogicJob.class,
                Options.CHUNK_SIZE, 100,
                "--input_file_path", "src/test/resources/geonames/cities10.txt");

        String xquery = "xquery version \"1.0-ml\";\n" +
                "declare namespace g = \"http://geonames.org\";\n" +
                "xdmp:estimate(cts:search(fn:doc(), cts:element-query(xs:QName(\"g:geoname\"), cts:and-query(()))))";

        String result = getClient().newServerEval().xquery(xquery).evalAs(String.class);
        assertEquals(10, Integer.parseInt(result));
        Fragment frag = getClientTestHelper().parseUri("http://geonames.org/geoname/4140963", "geonames");
        frag.assertElementValue("//geo:population", "601723");
        
            
    }
}
