package com.marklogic.spring.batch.item.http;


import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;


public class HttpJsonItemReader implements ItemStreamReader<String> {

    private URI uri;
    private RestTemplate restTemplate;
    private String jsonData;


    public HttpJsonItemReader(RestTemplate restTemplate, URI uri, String jsonData) {
        this.restTemplate = restTemplate;
        this.uri = uri;
        this.jsonData = jsonData;

    }
    @Override
    public String read() throws Exception {

        Boolean isJson = isStringValidJSON(jsonData);
        if (isJson){return jsonData;}
        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        ResponseEntity<String> response= restTemplate.getForEntity(uri,String.class);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            throw new ItemStreamException(response.getStatusCode() + " HTTP response is returned");
        };
        MediaType type = response.getHeaders().getContentType();
        if (MediaType.APPLICATION_JSON.equals(type)) {
            jsonData = response.getBody();
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }



    public static boolean isStringValidJSON(String jsonString) {
        return (isJSONStringObjectOrArray(jsonString) && isJSONStringParsable(jsonString));
    }

    private static boolean isJSONStringObjectOrArray(String jsonString) {
        try {
            JsonElement element = new JsonParser().parse(jsonString);

            return (element.isJsonObject() || element.isJsonArray());
        } catch (JsonSyntaxException jsonEx) {
            return false;
        }
    }

    private static boolean isJSONStringParsable(String jsonString) {
        try {
            org.codehaus.jackson.JsonParser parser =
                    new  ObjectMapper().getJsonFactory().createJsonParser(jsonString);
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
