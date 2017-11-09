package com.marklogic.spring.batch.item.http;

import org.jdom2.Document;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.ExpectedCount.manyTimes;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class HttpXmlItemReaderTest {

    RestTemplate mockRestTemplate;

    private String xml = "<stuff><entry>a</entry><entry>b</entry><entry>c</entry></stuff>";

    @Before
    public void before() {
        mockRestTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(mockRestTemplate).build();
        server.expect(manyTimes(), requestTo("/dummy")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(xml, MediaType.APPLICATION_XML));
    }

    @Test
    public void getXmlHttpResponseTest() throws Exception {
        HttpXmlItemReader itemReader = new HttpXmlItemReader(mockRestTemplate, new URI("/dummy"), "entry");
        itemReader.open(new ExecutionContext());
        Document doc = itemReader.read();
        assertThat("Expect value of 'a'", doc.getRootElement().getText().equals("a"));

        doc = itemReader.read();
        assertThat("Expect value of 'b'", doc.getRootElement().getText().equals("b"));

        doc = itemReader.read();
        assertThat("Expect value of 'c'", doc.getRootElement().getText().equals("c"));

        doc = itemReader.read();
        assertThat("Expect null", doc == null);
    }

}
