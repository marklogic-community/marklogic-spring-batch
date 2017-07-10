package com.marklogic.spring.batch.core.launch.support;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptCommandLineJobRunnerOptionsTest {

    @Test
    public void supportCommandLineJobRunnerOptionsTest() {
        CommandLineJobRunner runner = new CommandLineJobRunner();
        OptionParser parser = runner.buildOptionParser();
        OptionSet options = parser.parse("-options_file job.properties -jobPath com.marklogic.JobConfig -jobId job123");
        assertTrue(options.has("options_file"));
    }
}
