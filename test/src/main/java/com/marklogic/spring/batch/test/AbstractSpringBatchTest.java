package com.marklogic.spring.batch.test;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import com.marklogic.spring.batch.core.repository.MarkLogicSimpleJobRepository;
import com.marklogic.spring.batch.core.repository.dao.*;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;

/**
 * Base class for any "core" test.
 */
@ContextConfiguration(classes = {MarkLogicBatchConfiguration.class})
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {

    protected ApplicationContext applicationContext;

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setDatabaseClientProvider(applicationContext.getBean("databaseClientProvider", DatabaseClientProvider.class));
        setXccTemplate(applicationContext.getBean("xccTemplate", XccTemplate.class));
    }

    @Override
    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    
    
}
