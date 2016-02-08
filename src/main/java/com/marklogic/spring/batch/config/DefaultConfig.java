package com.marklogic.spring.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("default")
@EnableBatchProcessing
public class DefaultConfig {

}
