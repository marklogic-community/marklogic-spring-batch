package com.marklogic.spring.batch.columnmap;

import com.marklogic.spring.batch.utils.MetadataReader;
import org.springframework.jdbc.core.ColumnMapRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class RowToColumnMapWithMetadataMapper extends ColumnMapRowMapper {

    private Map<String, Object> metadata;

    public RowToColumnMapWithMetadataMapper(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> mapOfColValues = super.mapRow(rs, rowNum);
        if (mapOfColValues.size() == 0) {
            return null;
        } else {
            mapOfColValues.put(MetadataReader.META_MAP_KEY, metadata);
            return mapOfColValues;
        }
    }

}
