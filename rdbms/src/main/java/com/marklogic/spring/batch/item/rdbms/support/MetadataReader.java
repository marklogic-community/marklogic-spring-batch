package com.marklogic.spring.batch.item.rdbms.support;

import org.springframework.batch.item.ItemStreamException;

import java.util.Map;

public interface MetadataReader {
    public static String PK_MAP_KEY = "%";
    public static String ORDER_MAP_KEY = "$";
    public static String TABLE_NAME_MAP_KEY = "*";
    public static String META_MAP_KEY = "^";

    public Map<String, Object> getTableMetadata(String tableName) throws ItemStreamException;
    public Map<String, Map<String, Object>> getMetadata();
}
