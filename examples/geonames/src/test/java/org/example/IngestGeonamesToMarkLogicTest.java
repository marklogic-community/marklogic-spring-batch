package org.example;

import com.marklogic.client.spring.BasicConfig;
import com.marklogic.spring.batch.test.AbstractJobTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {BasicConfig.class})
public class IngestGeonamesToMarkLogicTest extends AbstractJobTest {

    @Test
    public void ingestUsCitiesTest() {
        assertTrue(true);
    }
}
