package com.marklogic.spring.batch.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.io.Resource;

/**
 * Created by sanjuthomas on 5/24/16.
 */
public class JsonItemProcessor implements ItemProcessor<Resource, ObjectNode> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public ObjectNode process(Resource item) throws Exception {
        return MAPPER.readValue(item.getFile(), ObjectNode.class);
    }
}
