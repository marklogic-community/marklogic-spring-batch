package com.marklogic.spring.batch.samples.geonames;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.samples.geonames.IngestGeonamesToMarkLogicJobConfig.class})
public class IngestGeonamesToMarkLogicJobTest extends AbstractJobRunnerTest {

    @Test
    public void ingestCitiesTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addLong("chunk", 10L);
        jpb.addString("input_file_path", "src/test/resources/geonames/cities10.txt");
        JobExecution jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());

        String xquery = "xquery version \"1.0-ml\";\n" +
                "declare namespace g = \"http://geonames.org\";\n" +
                "xdmp:estimate(cts:search(fn:doc(), cts:element-query(xs:QName(\"g:geoname\"), cts:and-query(()))))";

        String result = getClient().newServerEval().xquery(xquery).evalAs(String.class);
        assertEquals(10, Integer.parseInt(result));
        Fragment frag = getClientTestHelper().parseUri("http://geonames.org/geoname/4140963", "geonames");
        frag.assertElementValue("//geo:population", "601723");
    }
}
