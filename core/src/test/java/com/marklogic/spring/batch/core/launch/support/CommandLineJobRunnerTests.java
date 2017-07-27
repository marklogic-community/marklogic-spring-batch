package com.marklogic.spring.batch.core.launch.support;

import org.junit.Ignore;
import org.junit.Test;

public class CommandLineJobRunnerTests {

    //ignore test since it is hosing up the continuous testing since CLJR calls exit()
    @Ignore
    @Test
    public void missingJobPathTest() throws Exception {
        String[] args = new String[] {"--job_path", "test", "--job_id", "123"};
        CommandLineJobRunner.main(args);
        //assertEquals(1, StubSystemExiter.status);
        String errorMessage = CommandLineJobRunner.getErrorMessage();
    }


}