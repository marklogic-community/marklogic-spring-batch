package com.marklogic.spring.batch;

/**
 * Defines all recognized command-line options for the Main program.
 */
public interface Options {

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
}
