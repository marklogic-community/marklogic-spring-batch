package com.marklogic.spring.batch.test;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {com.marklogic.spring.batch.test.JobRunnerContext.class})
public abstract class AbstractJobRunnerTest extends AbstractSpringTest {

    private JobLauncherTestUtils jobLauncherTestUtils;
    private ClientTestHelper clientTestHelper;
    private DatabaseClientConfig databaseClientConfig;
    private ApplicationContext applicationContext;

    protected boolean isMarkLogic9() {
        AdminConfig config = new AdminConfig(databaseClientConfig.getHost(), databaseClientConfig.getPassword());
        AdminManager mgr = new AdminManager(config);
        return mgr.getServerVersion().startsWith("9");
    }

    public JobLauncherTestUtils getJobLauncherTestUtils() {
        return jobLauncherTestUtils;
    }

    @Autowired
    public void setJobLauncherTestUtils(JobLauncherTestUtils jobLauncherTestUtils) {
        this.jobLauncherTestUtils = jobLauncherTestUtils;
    }

    public ClientTestHelper getClientTestHelper() {
        if (clientTestHelper == null) {
            clientTestHelper = new ClientTestHelper();
            DatabaseClientProvider databaseClientProvider = new SimpleDatabaseClientProvider(databaseClientConfig);
            clientTestHelper.setDatabaseClientProvider(databaseClientProvider);
            clientTestHelper.setNamespaceProvider(new SpringBatchNamespaceProvider());
        }
        return clientTestHelper;

    }

    @Autowired
    @Qualifier("batchDatabaseClientConfig")
    public void setDatabaseClientConfig(DatabaseClientConfig databaseClientConfig) {
        this.databaseClientConfig = databaseClientConfig;
    }

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

}
