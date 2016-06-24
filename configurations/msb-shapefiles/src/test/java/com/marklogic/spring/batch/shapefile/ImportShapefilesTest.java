package com.marklogic.spring.batch.shapefile;

import com.marklogic.junit.ClientTestHelper;
import com.marklogic.spring.batch.test.AbstractJobTest;
import com.marklogic.spring.batch.test.JobProjectTestConfig;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;

@ContextConfiguration(classes = {MyConfig.class})
public class ImportShapefilesTest extends AbstractJobTest {

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

    /**
     * Verifies that the appropriate documents are created in the specified collection.
     */
    @Test
    public void test() {
        runJob(TestImportShapefilesConfig.class,
                "--input_file_path", "src/test/resources/shapefiles",
                "--output_collections", "shapefiles");

        ClientTestHelper h = newHelper(ClientTestHelper.class);
        h.assertInCollections("/shapefile/Colleges of Syria.zip", "shapefiles");
        h.assertInCollections("/shapefile/Colleges of Syria.zip.json", "shapefiles");
    }

    /**
     * Allows us to test everything but the connection to the OGRE web service.
     */
    @Configuration
    public static class TestImportShapefilesConfig extends ImportShapefilesConfig {
        @Override
        protected ShapefileProcessor shapefileProcessor(String ogreUrl) {
            OgreProxy testProxy = new OgreProxy() {
                @Override
                public String extractGeoJson(File file) throws IOException {
                    return MOCK_OGRE_RESPONSE;
                }
            };
            return new ShapefileProcessor(testProxy);
        }
    }

}
