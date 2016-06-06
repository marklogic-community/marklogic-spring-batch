package com.marklogic.spring.batch.core.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Created by sstafford on 6/5/2016.
 */
public class MarkLogicSimpleJobRepositoryConfig {

    public JsonNode getContentDatabase() {
        return contentDatabase;
    }

    JsonNode contentDatabase;

    public MarkLogicSimpleJobRepositoryConfig() {
        readConfig();
    }

    public void readConfig() {
        ObjectMapper objectMapper = new ObjectMapper();
        Resource db = new ClassPathResource("ml-config/databases/content-database.json");
        try {
            contentDatabase = objectMapper.readValue(db.getFile(), JsonNode.class);
        } catch (IOException ex) {

        }

    }
}
