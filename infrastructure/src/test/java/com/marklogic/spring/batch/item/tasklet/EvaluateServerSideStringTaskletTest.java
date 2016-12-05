package com.marklogic.spring.batch.item.tasklet;

import com.marklogic.client.spring.BasicConfig;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.batch.test.JobLauncherTestUtils;

@ContextConfiguration(classes = {BasicConfig.class})
public class EvaluateServerSideStringTaskletTest extends AbstractSpringTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Test
    public void evaluateXqueryStringTest() {
        String xquery = "xdmp:document-insert('hello.xml', <hello />)";
        Tasklet evalTasklet = new EvaluateServerSideStringTasklet(getClient(), EvaluateServerSideStringTasklet.ServerSideLanguage.XQUERY, xquery);
        Step evalXqueryStep = stepBuilderFactory.get("evalXquery").tasklet(evalTasklet).build();
        JobExecution jobExecution = jobLauncherTestUtils.launchStep("evalXquery");

    }
}
