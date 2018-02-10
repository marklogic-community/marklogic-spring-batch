import com.marklogic.spring.batch.test.AbstractJobRunnerTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.CoreMatchers.*;

@ContextConfiguration(classes = {YourJobConfig.class} )
@TestPropertySource(properties = { "marklogic.batch.config.enabled=false"})
public class YourJobWithNoMarkLogicJobRepoTest extends AbstractJobRunnerTest {

    JobExecution jobExecution;
    JobParametersBuilder jpb = new JobParametersBuilder();

    @Test
    public void jobRepoIsNullTest() throws Exception {
        givenYourJob();
        whenYourJobExecutes();
        thenMonstersExist();
    }

    @Before
    public void givenYourJob() {
        jpb.addString("output_collections", "monster");
    }

    public void whenYourJobExecutes() throws Exception {
         jobExecution = getJobLauncherTestUtils().launchJob(jpb.toJobParameters());
    }

    public void thenMonstersExist() {
        assertThat(jobExecution.getStatus(), is(BatchStatus.COMPLETED));
        getClientTestHelper().assertCollectionSize("Expecting 100 items in monster collection", "monster", 100);
    }

}
