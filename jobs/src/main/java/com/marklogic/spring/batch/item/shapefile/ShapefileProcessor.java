package com.marklogic.spring.batch.item.shapefile;

import com.marklogic.client.helper.LoggingObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.FileCopyUtils;

import java.io.File;

public class ShapefileProcessor extends LoggingObject implements ItemProcessor<File, ShapefileAndJson> {

    private String url = "http://ogre.adc4gis.com/convert";
    private HttpClient httpClient;

    /**
     * Using HttpClient, had trouble getting this to work with Spring's RestTemplate.
     *
     * @param item
     * @return
     * @throws Exception
     */
    @Override
    public ShapefileAndJson process(File item) throws Exception {
        if (httpClient == null) {
            httpClient = HttpClientBuilder.create().build();
        }
        HttpPost post = new HttpPost(url);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("upload", item);
        post.setEntity(builder.build());
        HttpResponse response = httpClient.execute(post);
        String json = new String(FileCopyUtils.copyToByteArray(response.getEntity().getContent()));
        return new ShapefileAndJson(item, json);
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
