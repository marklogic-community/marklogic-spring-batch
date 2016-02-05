package com.marklogic.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import com.marklogic.junit.spring.BasicTestConfig;

/**
 * Uses EnableBatchProcessing, which defaults to using ResourcelessTransactionManager if a DataSource cannot be found,
 * which is what we want for MarkLogic tests.
 */
@EnableBatchProcessing
public class TestConfig extends BasicTestConfig {

}
