package com.marklogic.spring.batch.core.repository;

import com.marklogic.mgmt.ManageClient;
import com.marklogic.mgmt.ManageConfig;
import com.marklogic.mgmt.restapis.RestApiManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DeployMarkLogicJobRepositoryTest {

    @Test
    public void deployMarkLogicJobRepositoryTest() {
        MarkLogicSimpleJobRepository.deploy("oscar", 8200, "admin", "admin");
    }
}
