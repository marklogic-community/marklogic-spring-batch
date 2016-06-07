package com.marklogic.spring.batch.job;

import org.junit.Test;

public class CorbJobTest extends AbstractJobTest {

    @Test
    public void testJob() {
        runJobWithMarkLogicJobRepository(CorbConfig.class,
                "--urisModule", "/ext/corb/uris.xqy",
                "--transformModule", "/ext/corb/process.xqy");
    }
}
