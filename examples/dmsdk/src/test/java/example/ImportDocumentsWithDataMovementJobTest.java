package example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.BasicConfig;
import com.marklogic.junit.ClientTestHelper;
import com.marklogic.mgmt.admin.AdminConfig;
import com.marklogic.mgmt.admin.AdminManager;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {BasicConfig.class})
public class ImportDocumentsWithDataMovementJobTest extends AbstractJobTest {
    
    private ClientTestHelper clientHelper;
    
    @Before
    public void setup() {
        DatabaseClientProvider clientProvider = getClientProvider();
        DatabaseClient client = clientProvider.getDatabaseClient();
        AdminConfig config = new AdminConfig(client.getHost(), client.getPassword());
        AdminManager mgr = new AdminManager(config);
        Assume.assumeTrue(mgr.getServerVersion().startsWith("9"));
        clientHelper = new ClientTestHelper();
        clientHelper.setDatabaseClientProvider(clientProvider);
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
        clientHelper.assertCollectionSize("Expect " + expectedCount + " docs in monster collection", "monster", expectedCount);
    }
    

}
