package com.marklogic.spring.batch.item.columnmap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.spring.batch.columnmap.ColumnMapSerializer;
import com.marklogic.spring.batch.columnmap.JacksonColumnMapSerializer;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class JacksonColumnMapSerializerTest extends Assert {

	private ColumnMapSerializer sut = new JacksonColumnMapSerializer();

	@Test
	public void test() throws Exception {
		Map<String, Object> map = new java.util.HashMap<>();
		map.put("color", "red");
		map.put("size", 10);
		map.put("boolean", true);

		String json = sut.serializeColumnMap(map, "ignored");

		JsonNode node = new ObjectMapper().readTree(json);
		assertEquals(true, node.get("boolean").asBoolean());
		assertEquals(10, node.get("size").asInt());
		assertEquals("red", node.get("color").asText());
	}
}
