package com.marklogic.spring.batch.item.shapefile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.spring.batch.item.processor.AbstractMarkLogicItemProcessor;
import com.marklogic.spring.batch.item.shapefile.support.OgreProxy;

import java.io.File;

/**
 * Depends on an instance of OgreProxy for extracting GeoJSON from the given File, which is assumed to be a
 * valid shapefile.
 */
public class ShapefileProcessor extends AbstractMarkLogicItemProcessor<File> {

    private OgreProxy ogreProxy;
    public ShapefileProcessor(OgreProxy ogreProxy) {
        this.ogreProxy = ogreProxy;
    }

    @Override
    public AbstractWriteHandle getContentHandle(File item) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode json = mapper.readTree(ogreProxy.extractGeoJson(item));
        return new JacksonHandle(json);
    }

}
