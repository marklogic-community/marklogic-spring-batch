package com.marklogic.spring.batch.config;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.Fragment;
import com.marklogic.spring.batch.config.sql.ReadAndWriteColumnMapsTest;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.Test;

public class ImportRdfFromFileTest extends AbstractJobTest {

    @Test
    public void test() {
        runJob(ImportRdfFromFileConfig.class,
                "--input_file_path", "src/test/resources/triple/test1.ttl",
                "--graph_name", "myTestGraph");

        String xml = getClient().newServerEval().xquery("collection('myTestGraph')").evalAs(String.class);
        Fragment f = parse(xml);
        f.assertElementExists("/sem:triples/sem:triple[sem:subject = 'http://example.org/kennedy/person1']");
    }
}
