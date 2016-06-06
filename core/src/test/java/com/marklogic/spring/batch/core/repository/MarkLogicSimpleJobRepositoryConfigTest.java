package com.marklogic.spring.batch.core.repository;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by sstafford on 6/5/2016.
 */
public class MarkLogicSimpleJobRepositoryConfigTest {

    @Test
    public void test() {
        MarkLogicSimpleJobRepositoryConfig config = new MarkLogicSimpleJobRepositoryConfig();
        assertEquals("spring-batch", config.getContentDatabase().get("database-name").textValue());
    }
}
