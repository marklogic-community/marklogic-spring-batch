package com.marklogic.spring.batch.item.rdf;

import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {ImportRdfFromFileJob.class})
public class ImportRdfFromFileJobTest extends AbstractJobTest {

    @Test
    public void test() {
        runJob(ImportRdfFromFileJob.class,
                "--input_file_path", "src/test/resources/triple/test1.ttl",
                "--graph_name", "myTestGraph");

        String xml = getClient().newServerEval().xquery("collection('myTestGraph')").evalAs(String.class);
        Fragment f = parse(xml);
        f.assertElementExists("/sem:triples/sem:triple[sem:subject = 'http://example.org/kennedy/person1']");
    }
}
