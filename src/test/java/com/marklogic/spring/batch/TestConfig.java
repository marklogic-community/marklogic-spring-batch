package com.marklogic.spring.batch;

import org.springframework.context.annotation.ComponentScan;

import com.marklogic.junit.spring.BasicTestConfig;

/**
 * Uses EnableBatchProcessing, which defaults to using ResourcelessTransactionManager if a DataSource cannot be found,
 * which is what we want for MarkLogic tests.
 */

@ComponentScan("com.marklogic.spring.batch.config")
public class TestConfig extends BasicTestConfig {

}
