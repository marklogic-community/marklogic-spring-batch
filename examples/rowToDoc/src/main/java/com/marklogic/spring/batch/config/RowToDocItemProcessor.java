package com.marklogic.spring.batch.config;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.MarkLogicWriteHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.columnmap.ColumnMapMerger;
import com.marklogic.spring.batch.columnmap.ColumnMapSerializer;
import com.marklogic.spring.batch.columnmap.DefaultColumnMapMerger;
import com.marklogic.spring.batch.columnmap.DefaultStaxColumnMapSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RowToDocItemProcessor implements ItemProcessor<Map<String, Object>, DocumentWriteOperation> {

    // Configurable
    private ColumnMapSerializer columnMapSerializer;
    private ColumnMapMerger columnMapMerger;
    private String rootElementName;

    // Internal state
    private Map<Object, Map<String, Object>> recordMap = new HashMap<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public RowToDocItemProcessor() {
        super();
        if (columnMapSerializer == null) {
            columnMapSerializer = new DefaultStaxColumnMapSerializer();
        }

        if (columnMapMerger == null) {
            columnMapMerger = new DefaultColumnMapMerger();
        }
    }

    public RowToDocItemProcessor(ColumnMapSerializer columnMapSerializer, ColumnMapMerger columnMapMerger) {

    }

    @Override
    public DocumentWriteOperation process(Map<String, Object> item) throws Exception {
        Set<Object> idsToIgnore = new HashSet<>();
        String idKey = item.keySet().iterator().next();
        Object id = item.get(idKey);
        idsToIgnore.add(id);
        Map<String, Object> existingColumnMap = recordMap.get(id);
        if (existingColumnMap != null && columnMapMerger != null) {
            columnMapMerger.mergeColumnMaps(existingColumnMap, item);
        } else {
            recordMap.put(id, item);
        }
        return rowToMarkLogicItemWriter(idsToIgnore);

    }

    private DocumentWriteOperation rowToMarkLogicItemWriter(Set<Object> idsToIgnore) {
        MarkLogicWriteHandle handle = null;
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
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
                    handle = new MarkLogicWriteHandle(uri, metadata, new StringHandle(content));
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
        recordIds.removeAll(idsToRemove);
        return handle;
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

    public void setColumnMapSerializer(ColumnMapSerializer columnMapSerializer) {
        this.columnMapSerializer = columnMapSerializer;
    }

    public void setColumnMapMerger(ColumnMapMerger columnMapMerger) {
        this.columnMapMerger = columnMapMerger;
    }

    public String getRootElementName() {
        return rootElementName;
    }

    public void setRootElementName(String rootElementName) {
        this.rootElementName = rootElementName;
    }

}
