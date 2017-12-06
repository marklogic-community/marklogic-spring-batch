import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import com.marklogic.spring.batch.test.JobRunnerContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { YourTwoStepJobConfig.class, JobRunnerContext.class } )
public class TwoStepJobTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void runJobTest() throws Exception {
        JobParametersBuilder jpb = new JobParametersBuilder();
        jpb.addString("output_collections", "monster");
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jpb.toJobParameters());
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
}
