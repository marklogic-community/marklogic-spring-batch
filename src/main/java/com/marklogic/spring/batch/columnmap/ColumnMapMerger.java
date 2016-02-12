package com.marklogic.spring.batch.columnmap;

import java.util.Map;

/**
 * Defines how two column maps - produced by Spring JDBC's ColumnMapRowMapper - should be merged together. This is
 * necessary when a SQL query uses a JOIN to read from multiple tables, and in the case of a 1:many relationship, there
 * will be N rows for each row related to a row from the first table. In that scenario, we want to merge those rows into
 * a single Map so they can be written as a single document to MarkLogic.
 */
public interface ColumnMapMerger {

    public void mergeColumnMaps(Map<String, Object> targetColumnMap, Map<String, Object> sourceColumnMap);
}
