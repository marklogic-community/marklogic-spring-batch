package org.example;

import com.marklogic.client.spring.BasicConfig;
import com.marklogic.example.IngestGeonamesToMarkLogicConfig;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {BasicConfig.class})
public class IngestGeonamesToMarkLogicTest extends AbstractJobTest {

    @Test
    public void ingestCitiesTest() {
        runJobWithMarkLogicJobRepository(IngestGeonamesToMarkLogicConfig.class, "--chunk", 100);
        String xquery = "xquery version \"1.0-ml\";\n" +
                "declare namespace g = \"http://geonames.org\";\n" +
                "xdmp:estimate(cts:search(fn:doc(), cts:element-query(xs:QName(\"g:geoname\"), cts:and-query(()))))";
        String result = getClient().newServerEval().xquery(xquery).evalAs(String.class);
        assertEquals("Expecting 1251 results from US Cities East file", 1251, Integer.parseInt(result));
    }
}
