package com.marklogic.spring.batch.columnmap;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Little unit test to help verify that we can process and merge column maps correctly.
 * 
 * @author rrudin
 *
 */
public class ProcessAndMergeColumnMapsTest extends Assert {

    private DefaultColumnMapMerger columnMapMerger = new DefaultColumnMapMerger();
    private PathAwareColumnMapProcessor columnMapProcessor = new PathAwareColumnMapProcessor();

    @Test
    public void test() throws Exception {
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("id", "123");
        record.put("name", "Jane");
        record.put("address/street", "123 Main St");
        record.put("address/city", "Falls Church");

        record = columnMapProcessor.process(record);

        System.out.println(record);

        assertEquals("123", record.get("id"));
        assertEquals("Jane", record.get("name"));
        Map<String, Object> address = (Map<String, Object>) record.get("address");
        assertNotNull(address);
        assertEquals("123 Main St", address.get("street"));
        assertEquals("Falls Church", address.get("city"));

        Map<String, Object> record2 = new LinkedHashMap<>();
        record2.put("id", "123");
        record2.put("name", "Jane");
        record2.put("address/street", "456 Main St");
        record2.put("address/city", "Arlington");
        record2 = columnMapProcessor.process(record2);
        System.out.println(record2);

        columnMapMerger.mergeColumnMaps(record, record2);

        System.out.println(record);

        assertEquals("123", record.get("id"));
        assertEquals("Jane", record.get("name"));
        List<Map<String, Object>> list = (List<Map<String, Object>>) record.get("address");
        assertEquals(2, list.size());
        address = list.get(0);
        assertEquals("123 Main St", address.get("street"));
        assertEquals("Falls Church", address.get("city"));
        address = list.get(1);
        assertEquals("456 Main St", address.get("street"));
        assertEquals("Arlington", address.get("city"));
    }

}
