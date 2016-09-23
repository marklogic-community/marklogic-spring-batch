package org.example;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.geonames.IngestGeonamesToMarkLogicConfig;
import com.marklogic.spring.batch.test.AbstractJobTest;
import com.marklogic.spring.batch.test.JobProjectTestConfig;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {JobProjectTestConfig.class})
public class IngestGeonamesToMarkLogicTest extends AbstractJobTest {

    ClientTestHelper testHelper = new ClientTestHelper();

    @Test
    public void ingestCitiesTest() {
        runJob(IngestGeonamesToMarkLogicConfig.class,
                Options.CHUNK_SIZE, 100,
                "--input_file_path", "src/test/resources/cities10.txt");

        String xquery = "xquery version \"1.0-ml\";\n" +
                "declare namespace g = \"http://geonames.org\";\n" +
                "xdmp:estimate(cts:search(fn:doc(), cts:element-query(xs:QName(\"g:geoname\"), cts:and-query(()))))";

        String result = getClient().newServerEval().xquery(xquery).evalAs(String.class);
        assertEquals(10, Integer.parseInt(result));
        
        testHelper.setDatabaseClientProvider(getClientProvider());
        testHelper.setNamespaceProvider(new GeonamesNamespaceProvider());
        Fragment frag = testHelper.parseUri("http://geonames.org/geoname/4140963", "geonames");
        frag.assertElementValue("//geo:population", "601723");
        
            
    }
}
