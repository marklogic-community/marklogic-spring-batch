package com.marklogic.spring.batch.columnmap;

import java.util.Map;

/**
 * Strategy interface for how a Spring "column map" can be written to a String of JSON or XML or, really, whatever you
 * want.
 */
public interface ColumnMapSerializer {

    String serializeColumnMap(Map<String, Object> columnMap, String rootLocalName);
}
