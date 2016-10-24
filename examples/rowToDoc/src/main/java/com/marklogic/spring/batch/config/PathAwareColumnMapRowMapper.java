package com.marklogic.spring.batch.config;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class PathAwareColumnMapRowMapper implements RowMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Map<String, Object> mapOfColValues = createColumnMap(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
            Object obj = getColumnValue(rs, i);
            mapOfColValues.put(key, obj);
        }

        Map<String, Object> newColumnMap = new LinkedHashMap<>();
        for (String key : mapOfColValues.keySet()) {
            if (key.contains("/")) {
                String[] paths = key.split("/");
                Object o = newColumnMap.get(paths[0]);
                // TODO Make this work for more than 1 forward slash
                if (o != null) {
                    if (o instanceof Map) {
                        Map<String, Object> map = (Map<String, Object>) o;
                        map.put(paths[1], mapOfColValues.get(key));
                    } else {
                        throw new IllegalStateException(
                                "The root of a path cannot equal any other column name: " + key);
                    }
                } else {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put(paths[1], mapOfColValues.get(key));
                    newColumnMap.put(paths[0], map);
                }
            } else {
                newColumnMap.put(key, mapOfColValues.get(key));
            }
        }
        return newColumnMap;
    }

    /**
     * Create a Map instance to be used as column map.
     * <p>By default, a linked case-insensitive Map will be created.
     * @param columnCount the column count, to be used as initial
     * capacity for the Map
     * @return the new Map instance
     * @see org.springframework.util.LinkedCaseInsensitiveMap
     */
    protected Map<String, Object> createColumnMap(int columnCount) {
        return new LinkedCaseInsensitiveMap<>(columnCount);
    }

    /**
     * Determine the key to use for the given column in the column Map.
     * @param columnName the column name as returned by the ResultSet
     * @return the column key to use
     * @see java.sql.ResultSetMetaData#getColumnName
     */
    protected String getColumnKey(String columnName) {
        return columnName;
    }

    /**
     * Retrieve a JDBC object value for the specified column.
     * <p>The default implementation uses the {@code getObject} method.
     * Additionally, this implementation includes a "hack" to get around Oracle
     * returning a non standard object for their TIMESTAMP datatype.
     * @param rs is the ResultSet holding the data
     * @param index is the column index
     * @return the Object returned
     * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue
     */
    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index);
    }
}
