package com.marklogic.spring.batch;

/**
 * Defines all recognized command-line options for the Main program.
 */
public interface Options {

    String HELP = "help";

    String HOST = "host";
    String PORT = "port";
    String USERNAME = "username";
    String PASSWORD = "password";

    String JOB_REPOSITORY_HOST = "jrHost";
    String JOB_REPOSITORY_PORT = "jrPort";
    String JOB_REPOSITORY_USERNAME = "jrUsername";
    String JOB_REPOSITORY_PASSWORD = "jrPassword";

    String CONFIG = "config";
    String JOB = "job";
    String CHUNK_SIZE = "chunk";

    String DEPLOY = "deployJobRepo";
    String UNDEPLOY = "undeployJobRepo";

    String JDBC_DRIVER = "jdbcDriver";
    String JDBC_URL = "jdbcUrl";
    String JDBC_USERNAME = "jdbcUsername";
    String JDBC_PASSWORD = "jdbcPassword";
}
