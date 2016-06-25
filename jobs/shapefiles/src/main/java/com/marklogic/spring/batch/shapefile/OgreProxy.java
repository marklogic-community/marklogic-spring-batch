package com.marklogic.spring.batch.shapefile;

import java.io.File;
import java.io.IOException;

public interface OgreProxy {

    /**
     * @param file requiring a File, as the httpmime library didn't seem to work properly when submitting
     *             a byte array or an InputStream
     * @return
     */
    String extractGeoJson(File file) throws IOException;

}
