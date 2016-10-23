package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.columnmap.ColumnMapMerger;
import com.marklogic.spring.batch.columnmap.ColumnMapSerializer;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RowToDocItemProcessor implements ItemProcessor<Map<String, Object>, MarkLogicItemWriter> {

    // Configurable
    private ColumnMapSerializer columnMapSerializer;
    private ColumnMapMerger columnMapMerger;
    private String rootElementName;

    // Internal state
    private Map<Object, Map<String, Object>> recordMap = new HashMap<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public MarkLogicItemWriter process(Map<String, Object> item) throws Exception {
        Set<Object> idsToIgnore = new HashSet<>();
        String idKey = item.keySet().iterator().next();
        Object idValue = item.get(idKey);
        idsToIgnore.add(idValue);
        Map<String, Object> existingColumnMap = recordMap.get(idValue);
        if (existingColumnMap != null && columnMapMerger != null) {
            columnMapMerger.mergeColumnMaps(existingColumnMap, item);
        } else {
            recordMap.put(idValue, item);
        }
        Set<Object> recordIds = recordMap.keySet();
        Set<Object> idsToRemove = new HashSet<>();
        for (Object id : recordIds) {
            if (idsToIgnore == null || !idsToIgnore.contains(id)) {
                Map<String, Object> columnMap = recordMap.get(id);
                try {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Writing record: " + columnMap);
                    }
                    String content = columnMapSerializer.serializeColumnMap(columnMap, this.rootElementName, null);
                    String uri = generateUri(content, id);
                    //set.add(uri, buildMetadata(), new StringHandle(content));
                    if (logger.isDebugEnabled()) {
                        logger.debug("Writing URI: " + uri + "; content: " + content);
                    }
                    idsToRemove.add(id);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Ignoring ID for now: " + id);
                }
            }
        }
/*
        if (!set.isEmpty()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Writing set of documents");
            }
            mgr.write(set);
            if (logger.isDebugEnabled()) {
                logger.debug("Finished writing set of documents");
            }
        }
        recordIds.removeAll(idsToRemove);
        */
        return null;
    }

    protected String generateUri(String content, Object id) {
        String uri = "/" + this.rootElementName + "/" + id;
        if (content.startsWith("{")) {
            return uri += ".json";
        } else if (content.startsWith("<")) {
            return uri += ".xml";
        }
        return uri;
    }
}
