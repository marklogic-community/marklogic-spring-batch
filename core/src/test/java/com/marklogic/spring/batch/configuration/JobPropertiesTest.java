package com.marklogic.spring.batch.configuration;

import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.configuration.JobProperties;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {
        com.marklogic.junit.spring.BasicTestConfig.class,
        com.marklogic.spring.batch.configuration.MarkLogicBatchConfiguration.class
})
public class JobPropertiesTest extends AbstractSpringTest {

    @Autowired
    JobProperties props;

    @Test
    public void propertiesTest() {
        assertNotNull(props);

        //assertEquals("localhost", props.getTargetDatabaseClientConfiguration().getHost());
        assertEquals(8201, props.getDatabaseClientConfig().getPort());
        assertEquals("spring-batch-admin", props.getDatabaseClientConfig().getUsername());
        assertEquals("password", props.getDatabaseClientConfig().getPassword());
    }

}
