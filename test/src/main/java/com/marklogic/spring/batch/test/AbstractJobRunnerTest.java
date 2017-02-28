package com.marklogic.spring.batch.test;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        com.marklogic.spring.batch.test.JobRunnerContext.class,
        com.marklogic.spring.batch.config.MarkLogicApplicationContext.class})
public abstract class AbstractJobRunnerTest extends AbstractSpringTest {

    private JobLauncherTestUtils jobLauncherTestUtils;
    private ClientTestHelper clientTestHelper;
    private DatabaseClientConfig databaseClientConfig;

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
        return clientTestHelper;
    }

    @Autowired
    public void setClientTestHelper(ClientTestHelper clientTestHelper) {
        this.clientTestHelper = clientTestHelper;

    }

    @Autowired
    public void setDatabaseClientConfig(DatabaseClientConfig databaseClientConfig) {
        this.databaseClientConfig = databaseClientConfig;
    }

}
