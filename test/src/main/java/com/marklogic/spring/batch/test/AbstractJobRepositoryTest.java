package com.marklogic.spring.batch.test;

import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import com.marklogic.spring.batch.core.explore.support.MarkLogicJobExplorerFactoryBean;
import com.marklogic.spring.batch.core.repository.support.MarkLogicJobRepositoryFactoryBean;
import com.marklogic.spring.batch.core.repository.support.MarkLogicJobRepositoryProperties;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration(classes = {MarkLogicBatchConfiguration.class, MarkLogicJobRepositoryProperties.class })
@TestPropertySource(value = "classpath:job.properties")
public abstract class AbstractJobRepositoryTest extends AbstractSpringTest {

    protected ApplicationContext applicationContext;
    private XccTemplate xccTemplate;
    protected MarkLogicJobRepositoryProperties properties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setDatabaseClientProvider(applicationContext.getBean(
                "markLogicJobRepositoryDatabaseClientProvider", DatabaseClientProvider.class));
        setXccTemplate(this.xccTemplate);
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

    @Autowired
    public void setMarkLogicJobRepositoryProperties(MarkLogicJobRepositoryProperties properties) {
        this.properties = properties;
    }

    protected JobRepository getJobRepository() throws Exception {
        MarkLogicJobRepositoryFactoryBean factory = new MarkLogicJobRepositoryFactoryBean();
        factory.setDatabaseClient(getClientProvider().getDatabaseClient());
        factory.setMarkLogicJobRepositoryProperties(properties);
        factory.setTransactionManager(new ResourcelessTransactionManager());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    protected JobExplorer getJobExplorer() throws Exception {
        MarkLogicJobExplorerFactoryBean factory = new MarkLogicJobExplorerFactoryBean();
        factory.setDatabaseClient(getClientProvider().getDatabaseClient());
        factory.setMarkLogicJobRepositoryProperties(properties);
        return factory.getObject();
    }

    protected MarkLogicJobRepositoryProperties getMarkLogicJobRepositoryProperties() {
        return properties;
    }




}
