package com.marklogic.spring.batch.core.repository;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by sstafford on 6/5/2016.
 */
public class MarkLogicSimpleJobRepositoryConfigTest {

    @Test
    public void test() {
        MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig();
        JsonNode node = config.getRestApiConfig().get("rest-api");
        assertEquals("spring-batch", node.get("name").textValue());
        assertEquals("spring-batch-content", node.get("database").textValue());
    }
}
