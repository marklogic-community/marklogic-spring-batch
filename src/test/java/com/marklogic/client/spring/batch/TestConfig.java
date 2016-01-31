package com.marklogic.client.spring.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import com.marklogic.client.spring.DatabaseConfig;

/**
 * Uses EnableBatchProcessing, which defaults to using ResourcelessTransactionManager if a DataSource cannot be found,
 * which is what we want for MarkLogic tests.
 */
@EnableBatchProcessing
public class TestConfig extends DatabaseConfig {

}
