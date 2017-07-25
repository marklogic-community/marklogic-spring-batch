package com.marklogic.spring.batch.item.rdf;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Test;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {ImportRdfFromFileJob.class})
public class ImportRdfFromFileJobTest extends AbstractJobRunnerTest {

    @Test
    public void test() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("input_file_path", "src/test/resources/triple/test1.ttl");
        jpb.addString("graph_name", "myTestGraph");
        getJobLauncherTestUtils().launchJob(jpb.toJobParameters());

        String xml = getClient().newServerEval().xquery("collection('myTestGraph')").evalAs(String.class);
        Fragment f = parse(xml);
        f.assertElementExists("/sem:triples/sem:triple[sem:subject = 'http://example.org/kennedy/person1']");
    }
}
