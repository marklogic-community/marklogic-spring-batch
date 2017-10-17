package com.marklogic.spring.batch.item.shapefile.support;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

/**
 * Using HttpClient, had trouble getting this to work with Spring's RestTemplate.
 */
public class HttpClientOgreProxy implements OgreProxy {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String url;
    private HttpClient httpClient;

    public HttpClientOgreProxy() {
        this("http://ogre.adc4gis.com/convert");
    }

    public HttpClientOgreProxy(String url) {
        this.url = url;
        this.httpClient = HttpClientBuilder.create().build();
    }

    @Override
    public String extractGeoJson(File file) throws IOException {
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("upload", file);
        post.setEntity(builder.build());
        HttpResponse response = httpClient.execute(post);
        return new String(FileCopyUtils.copyToByteArray(response.getEntity().getContent()));
    }
}
