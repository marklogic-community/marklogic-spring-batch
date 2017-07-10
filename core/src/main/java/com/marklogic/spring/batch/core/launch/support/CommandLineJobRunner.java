/*
* Copyright 2006-2013 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.marklogic.spring.batch.core.launch.support;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineJobRunner {
    
    protected static final Logger logger = LoggerFactory.getLogger(CommandLineJobRunner.class);

    public static void main(String[] args) throws Exception {
        new CommandLineJobRunner().start(args);

    }

    public void start(String[] args) {
        parseOptions(args);

        logger.info("COMPLETE");


    }


    protected OptionSet parseOptions(String[] args) {
        OptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(args);

        Properties props = new Properties();
        if (options.has(Options.OPTIONS_FILE)) {
            String path = options.valueOf(Options.OPTIONS_FILE).toString();

            try {
                props.load(new FileReader(new File(path)));
                for (String name : props.stringPropertyNames()) {
                    logger.debug(name + ": " + props.getProperty(name));
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to read options from properties file: " + path + "; cause: " + e.getMessage(), e);
            }

            List<String> list = new ArrayList<>();
            for (String key : props.stringPropertyNames()) {
                list.add(key);
                list.add(props.getProperty(key));
            }
            parser.parse(list.toArray(new String[]{}));
        } else {

        }
        if (options.has(Options.HELP)) {
            //printHelp(parser, options, args);
        }
        return null;
    }

    protected OptionParser buildOptionParser() {
        OptionParser parser = new OptionParser();
        parser.acceptsAll(Arrays.asList("h", Options.HELP), "Show help").forHelp();

        parser.accepts("options_file", "Path to a Java-style properties file that defines additional options").withRequiredArg();

        parser.accepts("jobPath", "application context containing a Job").withRequiredArg().defaultsTo("JobConfig");
        parser.accepts("jobId", "name of the job or the id of a job execution (for -stop, -abandon or -restart").withRequiredArg().defaultsTo("job");
        //parser.accepts("restart", "restart the last failed execution").withOptionalArg();
        //parser.accepts("stop", "stop a running execution");
        //parser.accepts("abandon", "abandon a stopped execution");
        //parser.accepts("next", "start the next in a sequence according to the increment for a job");



        parser.accepts("chunk_size", "chunk size for a step").withRequiredArg();

        parser.accepts("host", "Hostname of the destination MarkLogic Server").withRequiredArg().defaultsTo("localhost");
        parser.accepts("port", "Port number of the destination MarkLogic Server. There should be an XDBC App Server on this port. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
        parser.accepts("username", "The MarkLogic user to authenticate as against the given host and port").withRequiredArg().defaultsTo("admin");
        parser.accepts("password", "The password for the MarkLogic user").withRequiredArg().defaultsTo("admin");


/*
       parser.accepts(Options.DATABASE, "The name of the destination database. Default: The database associated with the destination App Server identified by -host and -port.").withRequiredArg();

        parser.accepts(Options.LIST, "List all of the Spring Configuration classes on the classpath");
        parser.accepts(Options.BASE_PACKAGE, "The optional base package to use when using --list to find Spring Configuration classes").withRequiredArg();

        parser.accepts(Options.CONFIG, "The fully qualified classname of the Spring Configuration class to register").withRequiredArg();
        parser.accepts(Options.JOB, "The name of the Spring Batch Job bean to run").withRequiredArg();

        parser.accepts(Options.JOB_REPOSITORY_NAME, "Name of the REST API server for the MarkLogic JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_HOST, "Hostname of the MarkLogic Server for the JobRepository").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_PORT, "Port number of the App Server for the JobRepository. The App Server must not be SSL-enabled.").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
        parser.accepts(Options.JOB_REPOSITORY_USERNAME, "The MarkLogic user to authenticate as against JobRepository App Server").withRequiredArg().defaultsTo("admin");
        parser.accepts(Options.JOB_REPOSITORY_PASSWORD, "The password for the JobRepository MarkLogic user").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_DATABASE, "The name of the JobRepository database. Default: The database associated with the destination App Server identified by -jrHost and -jrPort.").withRequiredArg();
        parser.accepts(Options.JOB_REPOSITORY_AUTHENTICATION, "The authentication to use for the app server on the given JobRepository port").withRequiredArg();

        */
        parser.allowsUnrecognizedOptions();

        return parser;
    }

}