package com.marklogic.spring.batch.item.shapefile;

import com.marklogic.client.helper.LoggingObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;

/**
 * Using HttpClient, had trouble getting this to work with Spring's RestTemplate.
 */
public class HttpClientOgreProxy extends LoggingObject implements OgreProxy {

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
