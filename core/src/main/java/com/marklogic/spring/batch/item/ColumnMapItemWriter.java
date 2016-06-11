package com.marklogic.spring.batch.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.marklogic.client.document.GenericDocumentManager;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.io.StringHandle;
import com.marklogic.spring.batch.columnmap.ColumnMapMerger;
import com.marklogic.spring.batch.columnmap.ColumnMapSerializer;
import com.marklogic.spring.batch.columnmap.DefaultColumnMapMerger;
import com.marklogic.spring.batch.columnmap.DefaultStaxColumnMapSerializer;

/**
 * Features this can provide:
 * <ol>
 * <li>Assume the first column is the ID column, but provide a property to allow for a column name.</li>
 * <li>Provide a strategy interface for generating XML element names based on column names.</li>
 * </ol>
 */
public class ColumnMapItemWriter extends AbstractDocumentWriter implements ItemWriter<Map<String, Object>>, ItemStream {

    // Configurable
    private ColumnMapSerializer columnMapSerializer;
    private ColumnMapMerger columnMapMerger;
    private String rootElementName;

    // Internal state
    private GenericDocumentManager mgr;
    private Map<Object, Map<String, Object>> recordMap = new HashMap<>();

    public ColumnMapItemWriter(DatabaseClient client, String rootElementName) {
        this.mgr = client.newDocumentManager();
        this.rootElementName = rootElementName;
    }

    /**
     * So what we need to do is, given an ID, we need to see if there's already a Map for that ID. If there is, we need
     * to merge the data from the new item into the existing item.
     * 
     * When we get a column label like address/street, we need to tokenize it...
     */
    @Override
    public void write(List<? extends Map<String, Object>> items) throws Exception {
        Set<Object> idsToIgnore = new HashSet<>();
        for (Map<String, Object> columnMap : items) {
            String idKey = columnMap.keySet().iterator().next();
            Object id = columnMap.get(idKey);
            idsToIgnore.add(id);
            Map<String, Object> existingColumnMap = recordMap.get(id);
            if (existingColumnMap != null && columnMapMerger != null) {
                columnMapMerger.mergeColumnMaps(existingColumnMap, columnMap);
            } else {
                recordMap.put(id, columnMap);
            }
        }
        writeRecords(idsToIgnore);
    }

    private void writeRecords(Set<Object> idsToIgnore) {
        DocumentWriteSet set = mgr.newWriteSet();
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
                    set.add(uri, buildMetadata(), new StringHandle(content));
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
    }

    /**
     * Does some hacking to determine if we need a json or xml suffix.
     *
     * TODO UriGenerator needs more support; it needs to be smart enough to determine the suffix
     * itself.
     *
     * @param content
     * @param id
     * @return
     */
    protected String generateUri(String content, Object id) {
        String uri = "/" + this.rootElementName + "/" + id;
        if (content.startsWith("{")) {
            return uri += ".json";
        } else if (content.startsWith("<")) {
            return uri += ".xml";
        }
        return uri;
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (columnMapSerializer == null) {
            columnMapSerializer = new DefaultStaxColumnMapSerializer();
        }

        if (columnMapMerger == null) {
            columnMapMerger = new DefaultColumnMapMerger();
        }
    }

    /**
     * This close method from ItemStream gives us a way to write all the remaining records in our map after all the rows
     * have been read from the SQL database.
     */
    @Override
    public void close() throws ItemStreamException {
        if (logger.isDebugEnabled()) {
            logger.debug("Closing Writer, and writing remaining records");
        }
        writeRecords(null);
    }

    public void setColumnMapSerializer(ColumnMapSerializer columnMapSerializer) {
        this.columnMapSerializer = columnMapSerializer;
    }

    public void setColumnMapMerger(ColumnMapMerger columnMapMerger) {
        this.columnMapMerger = columnMapMerger;
    }
}