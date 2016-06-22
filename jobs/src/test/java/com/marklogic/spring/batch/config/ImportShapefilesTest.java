package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.item.shapefile.ImportShapefilesConfig;
import org.junit.Test;

public class ImportShapefilesTest extends AbstractJobsJobTest {

    private final static String MOCK_OGRE_RESPONSE = "{\n" +
            "  \"type\": \"FeatureCollection\",\n" +
            "  \"features\": [\n" +
            "    {\n" +
            "      \"type\": \"Feature\",\n" +
            "      \"properties\": {\n" +
            "        \"category\": \"Government and Public Services\",\n" +
            "        \"name\": \"College:Arab Academy for Science and Technologe and Maritime Transport\",\n" +
            "        \"ntype\": \"College\"\n" +
            "      },\n" +
            "      \"geometry\": {\n" +
            "        \"type\": \"Point\",\n" +
            "        \"coordinates\": [\n" +
            "          3982629.6882999986,\n" +
            "          4236277.941399999\n" +
            "        ]\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    @Test
    public void test() {
        runJob(ImportShapefilesConfig.class,
                "--input_file_path", "build/shapefiles");
    }

}
