package com.marklogic.spring.batch.item.http;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class HttpJsonItemReaderTest {
    RestTemplate mockRestTemplate;


    String json = "{\n" +
            "  \"name\" : \"Ronaldo\",\n" +
            "  \"sport\" : \"soccer\",\n" +
            "  \"age\" : 25,\n" +
            "  \"id\" : 121,\n" +
            "  \"lastScores\" : [ 2, 1, 3, 5, 0, 0, 1, 1 ]" +
            "}";
    ObjectMapper mapper = new ObjectMapper();

    private JsonNode actualObj = mapper.readTree(json);

    public HttpJsonItemReaderTest() throws IOException {
    }


    @Before
    public void before() {

        mockRestTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(mockRestTemplate).build();
        server.expect(manyTimes(), requestTo("/dummy")).andExpect(method(HttpMethod.GET)).
                andRespond(withSuccess(json, MediaType.APPLICATION_JSON));
    }

    @Test
    public void getJsonHttpResponseTest() throws Exception {
        HttpJsonItemReader itemReader = new HttpJsonItemReader(mockRestTemplate, new URI("/dummy"),actualObj);
        itemReader.open(new ExecutionContext());

        JsonNode jsonData1 = itemReader.read();
        assertThat(jsonData1.asText(), equalTo("Ronaldo"));

        JsonNode jsonData2 = itemReader.read();
        assertThat(jsonData2.asText(), equalTo("soccer"));

        JsonNode jsonData3 = itemReader.read();
        assertThat(jsonData3.asText(), equalTo("25"));

        JsonNode jsonData4 = itemReader.read();
        assertThat(jsonData4.asText(), equalTo("121"));

        JsonNode jsonData5 = itemReader.read();
        assertThat(jsonData5.isArray(), equalTo(true));
    }




}
