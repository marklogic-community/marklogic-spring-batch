package com.marklogic.spring.batch.test;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.ext.DatabaseClientConfig;
import com.marklogic.client.ext.helper.DatabaseClientProvider;
import com.marklogic.client.ext.spring.SimpleDatabaseClientProvider;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import com.marklogic.spring.batch.config.MarkLogicConfiguration;
import com.marklogic.xcc.template.XccTemplate;
import org.junit.Before;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@ContextConfiguration(classes = {com.marklogic.spring.batch.test.JobRunnerContext.class})
public abstract class AbstractJobRunnerTest extends AbstractSpringTest {

    private JobLauncherTestUtils jobLauncherTestUtils;
    private ClientTestHelper clientTestHelper;
    private DatabaseClientConfig databaseClientConfig;
    private ApplicationContext applicationContext;

    private DatabaseClientProvider databaseClientProvider;
    private DatabaseClientProvider markLogicJobRepositoryDatabaseClientProvider;

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

    @Autowired
    @Qualifier("targetDatabaseClientProvider")
    public void setDatabaseClientProvider(
            DatabaseClientProvider databaseClientProvider) {
        this.databaseClientProvider = databaseClientProvider;
    }

    @Autowired
    @Qualifier("markLogicJobRepositoryDatabaseClientProvider")
    public void setMarkLogicJobRepositoryDatabaseClientProvider(
            DatabaseClientProvider markLogicJobRepositoryDatabaseClientProvider) {
        this.markLogicJobRepositoryDatabaseClientProvider = markLogicJobRepositoryDatabaseClientProvider;
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

    @Override
    protected DatabaseClient getClient() {
        return databaseClientProvider.getDatabaseClient();
    }

    @Autowired
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
        setDatabaseClientProvider(this.databaseClientProvider);
    }

    @Before
    public void deleteDocumentsBeforeTestRuns() {
        ServerEvaluationCall evalCall = databaseClientProvider.getDatabaseClient().newServerEval();
        evalCall.xquery(getClearDatabaseXquery());
        evalCall.eval();

        evalCall = markLogicJobRepositoryDatabaseClientProvider.getDatabaseClient().newServerEval();
        evalCall.xquery(getClearDatabaseXquery());
        evalCall.eval();
    }

}
