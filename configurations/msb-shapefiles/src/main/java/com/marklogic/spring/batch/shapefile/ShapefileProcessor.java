package com.marklogic.spring.batch.shapefile;

import com.marklogic.client.helper.LoggingObject;
import org.springframework.batch.item.ItemProcessor;

import java.io.File;

/**
 * Depends on an instance of OgreProxy for extracting GeoJSON from the given File, which is assumed to be a
 * valid shapefile.
 */
public class ShapefileProcessor extends LoggingObject implements ItemProcessor<File, ShapefileAndJson> {

    private OgreProxy ogreProxy;

    public ShapefileProcessor(OgreProxy ogreProxy) {
        this.ogreProxy = ogreProxy;
    }

    @Override
    public ShapefileAndJson process(File item) throws Exception {
        return new ShapefileAndJson(item, ogreProxy.extractGeoJson(item));
    }
}
