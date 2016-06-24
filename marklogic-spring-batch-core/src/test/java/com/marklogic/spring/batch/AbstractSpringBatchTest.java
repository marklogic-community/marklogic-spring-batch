package com.marklogic.spring.batch;

import com.marklogic.client.spring.BasicConfig;
import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Base class for any "core" test.
 */
@ContextConfiguration(classes = {BasicConfig.class})
public abstract class AbstractSpringBatchTest extends AbstractSpringTest {

    @Override
    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

}
