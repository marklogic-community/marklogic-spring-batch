package com.marklogic.spring.batch.columnmap;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;

import com.marklogic.client.helper.LoggingObject;

/**
 * This processor will "expand" each column that has an XPath-style "/" in its name. This currently only supports one
 * level of nesting. It will combine every column that has the same path before a "/" into a new Map<String, Object>.
 * This allows for a hierarchy of data to be created which can match the XML that will eventually be generated.
 */
public class PathAwareColumnMapProcessor extends LoggingObject
        implements ItemProcessor<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> process(Map<String, Object> columnMap) throws Exception {
        Map<String, Object> newColumnMap = new LinkedHashMap<>();
        for (String key : columnMap.keySet()) {
            if (key.contains("/")) {
                String[] paths = key.split("/");
                Object o = newColumnMap.get(paths[0]);
                // TODO Make this work for more than 1 forward slash
                if (o != null) {
                    if (o instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) o;
                        map.put(paths[1], columnMap.get(key));
                    } else {
                        throw new IllegalStateException(
                                "The root of a path cannot equal any other column name: " + key);
                    }
                } else {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put(paths[1], columnMap.get(key));
                    newColumnMap.put(paths[0], map);
                }
            } else {
                newColumnMap.put(key, columnMap.get(key));
            }
        }
        return newColumnMap;
    }

}
