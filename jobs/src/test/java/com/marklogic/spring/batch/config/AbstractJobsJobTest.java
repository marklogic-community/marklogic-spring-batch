package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.test.AbstractJobTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Reuses the test project's AbstractJobTest class and defines a Spring config for
 * loading properties.
 */
@ContextConfiguration(classes = {JobTestConfig.class})
public abstract class AbstractJobsJobTest extends AbstractJobTest {
}
