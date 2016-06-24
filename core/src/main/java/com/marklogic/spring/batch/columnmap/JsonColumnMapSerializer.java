package com.marklogic.spring.batch.columnmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonColumnMapSerializer implements ColumnMapSerializer {

    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public String serializeColumnMap(Map<String, Object> columnMap, String rootLocalName, String rootNamespaceUri) {
        try {
            return mapper.writeValueAsString(columnMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
