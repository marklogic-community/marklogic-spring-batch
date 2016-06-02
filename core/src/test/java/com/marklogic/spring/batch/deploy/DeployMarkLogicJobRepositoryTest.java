package com.marklogic.spring.batch.deploy;

import org.junit.Test;

public class DeployMarkLogicJobRepositoryTest {

    @Test
    public void createRestApi() {
        new DeployMarkLogicJobRepository().createRestApi();
    }
}
