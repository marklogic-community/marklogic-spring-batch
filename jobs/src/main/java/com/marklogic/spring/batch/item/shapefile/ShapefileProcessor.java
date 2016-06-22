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

/**
 * Depends on an instance of OgreProxy for extracting GeoJSON from the given File, which is assumed to be a
 * valid shapefile.
 */
public class ShapefileProcessor extends LoggingObject implements ItemProcessor<File, ShapefileAndJson> {

    private OgreProxy ogreProxy;

    @Override
    public ShapefileAndJson process(File item) throws Exception {
        return new ShapefileAndJson(item, ogreProxy.extractGeoJson(item));
    }

    public void setOgreProxy(OgreProxy ogreProxy) {
        this.ogreProxy = ogreProxy;
    }
}
