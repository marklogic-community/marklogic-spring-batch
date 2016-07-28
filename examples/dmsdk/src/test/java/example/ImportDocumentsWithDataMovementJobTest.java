package example;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.spring.batch.test.AbstractJobTest;
import com.marklogic.spring.batch.test.JobProjectTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {JobProjectTestConfig.class})
public class ImportDocumentsWithDataMovementJobTest extends AbstractJobTest {
    
    private ClientTestHelper client;
    
    @Before
    public void setup() {
        client = new ClientTestHelper();
        client.setDatabaseClientProvider(getClientProvider());
    }
    
    @Test
    public void loadXmlDocumentsTest() {
        runJob(
            ImportDocumentsWithDataMovementConfig.class,
            "--input_file_path", "src/test/resources/data/*.xml",
            "--output_collections", "monster,sesameStreet");
        thenDocumentsInMonsterCollection(3);
    }
    
    public void thenDocumentsInMonsterCollection(int expectedCount) {
        client.assertCollectionSize("Expect " + expectedCount + " docs in monster collection", "monster", expectedCount);
    }
    

}
