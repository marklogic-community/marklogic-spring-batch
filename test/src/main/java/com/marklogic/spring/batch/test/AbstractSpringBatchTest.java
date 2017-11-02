package com.marklogic.spring.batch.test;

import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for any "core" test.
 */
@ContextConfiguration(classes = {MarkLogicBatchConfiguration.class, AbstractSpringBatchTest.TestConfig.class})
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {

    protected ApplicationContext applicationContext;
    protected BatchProperties batchProperties;

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

    @Override
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setDatabaseClientProvider(applicationContext.getBean("databaseClientProvider", DatabaseClientProvider.class));
        setXccTemplate(applicationContext.getBean("xccTemplate", XccTemplate.class));
    }

    public BatchProperties getBatchProperties() {
        return batchProperties;
    }

    @Autowired
    public void setBatchProperties(BatchProperties batchProperties) {
        this.batchProperties = batchProperties;
    }

    @Configuration
    @ComponentScan(basePackages = {"com.marklogic.spring.batch.config"})
    @PropertySource(value = "classpath:job.properties")
    public static class TestConfig {


    }


}
