package com.marklogic.spring.batch.item.http;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class HttpJsonItemReaserTest {
    RestTemplate mockRestTemplate;


    String json = "{\n" +
            "  \"name\" : \"Ronaldo\",\n" +
            "  \"sport\" : \"soccer\",\n" +
            "  \"age\" : 25,\n" +
            "  \"id\" : 121,\n" +
            "  \"lastScores\" : [ 2, 1, 3, 5, 0, 0, 1, 1 ]" +
            "}";


    List<Map<String, Object>> listJson = new ArrayList<Map<String, Object>>();


    @Before
    public void before() {

        mockRestTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(mockRestTemplate).build();
        server.expect(manyTimes(), requestTo("/dummy")).andExpect(method(HttpMethod.GET)).
                andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    @Test
    public void getJsonHttpResponseTest() throws Exception {
        HttpJsonItemReader itemReader = new HttpJsonItemReader(mockRestTemplate, new URI("/dummy"),json);
        itemReader.open(new ExecutionContext());
        String jsonData = itemReader.read();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonData);

        JsonNode jsonNode1 = actualObj.get("name");
        assertThat(jsonNode1.asText(), equalTo("Ronaldo"));
        assertNotNull(actualObj);
        assertThat(jsonData, equalTo(json));
    }




}
