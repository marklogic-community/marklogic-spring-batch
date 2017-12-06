package com.marklogic.spring.batch.test;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration(classes = {MarkLogicBatchConfiguration.class, BatchProperties.class})
@TestPropertySource(value = "classpath:job.properties")
public abstract class AbstractJobRepositoryTest extends AbstractSpringTest {

    protected ApplicationContext applicationContext;
    private BatchProperties batchProperties;
    private XccTemplate xccTemplate;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setDatabaseClientProvider(applicationContext.getBean(
                "markLogicJobRepositoryDatabaseClientProvider", DatabaseClientProvider.class));
        setXccTemplate(this.xccTemplate);
    }

    public BatchProperties getBatchProperties() {
        return batchProperties;
    }

    @Autowired
    public void setBatchProperties(BatchProperties batchProperties) {
        this.batchProperties = batchProperties;
    }

    @Autowired
    protected XccTemplate xccTemplate(
            DatabaseClientConfig markLogicJobRepositoryDatabaseClientConfig,
            @Value("${marklogic.jobrepo.database:mlJobRepo}") String databaseName) {
        xccTemplate = new XccTemplate(
                String.format("xcc://%s:%s@%s:%s/%s",
                        markLogicJobRepositoryDatabaseClientConfig.getUsername(),
                        markLogicJobRepositoryDatabaseClientConfig.getPassword(),
                        markLogicJobRepositoryDatabaseClientConfig.getHost(),
                        markLogicJobRepositoryDatabaseClientConfig.getPort(),
                        databaseName));
        return xccTemplate;
    }

}
