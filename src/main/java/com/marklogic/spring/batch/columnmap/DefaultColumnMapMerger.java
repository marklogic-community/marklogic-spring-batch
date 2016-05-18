package com.marklogic.spring.batch.columnmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Default implementation that does one simple thing - when the sourceColumnMap has a key that matches a key in the
 * targetColumnMap, but the values are different, this class will create a List and stuff both values in. If the target
 * value is already a List, the value from the source will be added to the list.
 */
public class DefaultColumnMapMerger implements ColumnMapMerger {

    @Override
    public void mergeColumnMaps(Map<String, Object> targetColumnMap, Map<String, Object> sourceColumnMap) {
        for (String key : sourceColumnMap.keySet()) {
            if (targetColumnMap.containsKey(key)) {
                Object newValue = sourceColumnMap.get(key);
                Object existingValue = targetColumnMap.get(key);
                if (newValue != null && !newValue.equals(existingValue)) {
                    // If they're not equal, assume we should construct a List (if we don't have it already) and add
                    // the new value
                    List<Object> list;
                    if (existingValue instanceof List) {
                        list = (List<Object>) existingValue;
                    } else {
                        list = new ArrayList<>();
                        list.add(existingValue);
                        targetColumnMap.put(key, list);
                    }
                    list.add(newValue);
                }
            } else {
                targetColumnMap.put(key, sourceColumnMap.get(key));
            }
        }
    }

}
