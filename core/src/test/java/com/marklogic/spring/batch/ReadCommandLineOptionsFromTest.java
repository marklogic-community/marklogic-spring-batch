package com.marklogic.spring.batch;

import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.junit.Test;

import java.util.List;

public class ReadCommandLineOptionsFromTest extends AbstractSpringBatchTest {

    @Test
    public void recognizedAndUnrecognizedOptionsInOptionsFile() {
        String[] args = new String[]{
                "--job", "test",
                "--custom", "value",
                "--options_file", "src/test/resources/options/sample-options.properties"};

        Main main = new Main();
        OptionSet options = main.parseOptions(main.buildOptionParser(), args);
        assertEquals("test", options.valueOf(Options.JOB));
        assertEquals("some-host", options.valueOf(Options.HOST));

        List<String> otherArgs = (List<String>) options.nonOptionArguments();
        assertEquals("--custom", otherArgs.get(0));
        assertEquals("value", otherArgs.get(1));
        assertEquals("--multi_line_property", otherArgs.get(2));
        assertEquals("Value on two lines", otherArgs.get(3));
    }

    @Test
    public void readFromMissingFile() {
        String[] args = new String[]{
                "--options_file", "src/test/resources/options/doesnt-exist.properties"};

        Main main = new Main();
        OptionParser parser = main.buildOptionParser();
        try {
            main.parseOptions(parser, args);
            fail("Expected parsing to fail because the options file doesn't exist");
        } catch (Exception e) {
            assertNotNull(e.getMessage());
        }
    }
}
