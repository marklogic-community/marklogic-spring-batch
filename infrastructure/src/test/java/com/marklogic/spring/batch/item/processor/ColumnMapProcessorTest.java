package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.io.MarkLogicWriteHandle;
import com.marklogic.spring.batch.columnmap.DefaultStaxColumnMapSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ColumnMapProcessorTest extends Assert {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private ColumnMapProcessor columnMapProcessor;

    @Test
    public void columnMapSimpleTest() throws Exception {
        columnMapProcessor = new ColumnMapProcessor(new DefaultStaxColumnMapSerializer());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sample", "value");

        MarkLogicWriteHandle handle = columnMapProcessor.process(map);
        logger.info(handle.getUri());
        assertNotNull(handle);

    }
}
