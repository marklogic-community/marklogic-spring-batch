package com.marklogic.spring.batch.core.launch.support;

import org.junit.Test;

public class CommandLineJobRunnerTests {

    @Test(expected = RuntimeException.class)
    public void missingJobPathTest() throws Exception {
        String[] args = new String[] {};
        CommandLineJobRunner.main(args);
        //assertEquals(1, StubSystemExiter.status);
        String errorMessage = CommandLineJobRunner.getErrorMessage();
    }


}