package com.marklogic.spring.batch.test;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.context.ApplicationContext;

public abstract class AbstractJobRepositoryTest extends AbstractSpringBatchTest {

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setDatabaseClientProvider(applicationContext.getBean("markLogicJobRepositoryDatabaseClientProvider", DatabaseClientProvider.class));
		setXccTemplate(applicationContext.getBean("markLogicJobRepositoryXccTemplate", XccTemplate.class));
    }

}
