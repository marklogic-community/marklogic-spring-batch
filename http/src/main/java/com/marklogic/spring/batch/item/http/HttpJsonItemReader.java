package com.marklogic.spring.batch.item.http;

import com.google.gson.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;


public class HttpJsonItemReader implements ItemStreamReader<JsonNode> {

    private URI uri;
    private String jsonString;
    private RestTemplate restTemplate;
    private JsonNode nodeElements;
    private Iterator<String> iterator ;
    private int count = 0;


    public HttpJsonItemReader(RestTemplate restTemplate, URI uri, JsonNode nodeElements) {
        this.restTemplate = restTemplate;
        this.uri = uri;
        this.nodeElements = nodeElements;

    }
    @Override
    public JsonNode read() throws Exception {
        JsonNode node = null;
        if (iterator.hasNext()) {
            String fieldName = iterator.next();
            node = nodeElements.get(fieldName).deepCopy();
            count++;
            };
        return node;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        ResponseEntity<String> response= restTemplate.getForEntity(uri,String.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new ItemStreamException(response.getStatusCode() + " HTTP response is returned");
        };
        MediaType type = response.getHeaders().getContentType();
        if (MediaType.APPLICATION_JSON.equals(type)) {
            jsonString = response.getBody();
            Boolean isJson = isStringValidJSON(jsonString);
            if (isJson){
                // create an ObjectMapper instance.
                ObjectMapper mapper = new ObjectMapper();
                // use the ObjectMapper to read the json string and create a tree
                try {
                    nodeElements = mapper.readTree(jsonString);
                    iterator = nodeElements.fieldNames();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        executionContext.putInt("count", count);
    }

    @Override
    public void close() throws ItemStreamException {

    }


    public static boolean isStringValidJSON(String jsonString) {
        return (isJSONStringObjectOrArray(jsonString) && isJSONStringParsable(jsonString));
    }

    public static boolean isJSONStringObjectOrArray(String jsonString) {
        try {
            JsonElement element = new JsonParser().parse(jsonString);

            return (element.isJsonObject() || element.isJsonArray());
        } catch (JsonSyntaxException jsonEx) {
            return false;
        }
    }

    public static boolean isJSONStringParsable(String jsonString) {
        try {

            com.fasterxml.jackson.core.JsonParser parser =
                    new  ObjectMapper().getFactory().createParser(jsonString);
            while(parser.nextToken() != null) {
            }
            return true;
        } catch (JsonParseException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

}
