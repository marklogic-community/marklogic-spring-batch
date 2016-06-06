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

    JsonNode contentDatabase;
    JsonNode restApiConfig;

    public JsonNode getContentDatabase() {
        return contentDatabase;
    }

    public JsonNode getRestApiConfig() {
        return restApiConfig;
    }


    public MarkLogicSimpleJobRepositoryConfig() {
        readConfig();
    }

    public void readConfig() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ClassPathResource db = new ClassPathResource("ml-config/databases/content-database.json");
            contentDatabase = objectMapper.readValue(db.getFile(), JsonNode.class);

            db = new ClassPathResource("ml-config/rest-api.json");
            restApiConfig = objectMapper.readValue(db.getFile(), JsonNode.class);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
