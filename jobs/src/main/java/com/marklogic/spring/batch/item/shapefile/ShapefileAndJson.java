package com.marklogic.spring.batch.item.shapefile;

import java.io.File;

public class ShapefileAndJson {

    public File file;
    public String json;

    public ShapefileAndJson(File file, String json) {
        this.file = file;
        this.json = json;
    }
}
