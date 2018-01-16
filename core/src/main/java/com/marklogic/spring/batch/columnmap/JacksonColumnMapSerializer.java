package com.marklogic.spring.batch.columnmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Simple implementation that uses Jackson's writeValueAsString method. The outputted JSON string can be customized by
 * passing in your own ObjectMapper implementation.
 */
public class JacksonColumnMapSerializer implements ColumnMapSerializer {

	private ObjectMapper objectMapper;

	public JacksonColumnMapSerializer() {
		this(new ObjectMapper());
	}

	public JacksonColumnMapSerializer(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public String serializeColumnMap(Map<String, Object> columnMap, String rootLocalName) {
		try {
			return objectMapper.writeValueAsString(columnMap);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Unable to write map as JSON: " + e.getMessage(), e);
		}
	}
}
