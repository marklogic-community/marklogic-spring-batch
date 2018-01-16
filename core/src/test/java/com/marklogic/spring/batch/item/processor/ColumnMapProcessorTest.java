package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.columnmap.DefaultStaxColumnMapSerializer;
import com.marklogic.spring.batch.columnmap.JacksonColumnMapSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ColumnMapProcessorTest extends Assert {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void columnMapSimpleXmlTest() throws Exception {
        ColumnMapProcessor columnMapProcessor = new ColumnMapProcessor(new DefaultStaxColumnMapSerializer());
        columnMapProcessor.setRootLocalName("test");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sample", "value");

        DocumentWriteOperation handle = columnMapProcessor.process(map);
        logger.info(handle.getUri());
        StringHandle strHandle = (StringHandle) handle.getContent();
        logger.info(strHandle.get());
        assertTrue("<test><sample>value</sample></test>".equals(strHandle.get()));
        assertNotNull(handle);

    }

    @Test
    public void getRootNameFromMapTest() throws Exception {
        ColumnMapProcessor columnMapProcessor = new ColumnMapProcessor(new DefaultStaxColumnMapSerializer());
        columnMapProcessor.setRootLocalName("type");

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("type", "junk");
        map.put("sample", "value");

        DocumentWriteOperation handle = columnMapProcessor.process(map);
        logger.info(handle.getUri());
        StringHandle strHandle = (StringHandle) handle.getContent();
        logger.info(strHandle.get());
        assertTrue("<junk><type>junk</type><sample>value</sample></junk>".equals(strHandle.get()));
        assertNotNull(handle);

    }

    @Test
    public void columnMapSimpleJsonTest() throws Exception {
        ColumnMapProcessor columnMapProcessor = new ColumnMapProcessor(new JacksonColumnMapSerializer());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("sample", "value");

        DocumentWriteOperation handle = columnMapProcessor.process(map);
        logger.info(handle.getUri());
        StringHandle strHandle = (StringHandle) handle.getContent();
        logger.info(strHandle.get());
        assertTrue("{\"sample\":\"value\"}".equals(strHandle.get()));
        assertNotNull(handle);

    }


}
