package com.marklogic.spring.batch.item.shapefile;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.spring.batch.item.shapefile.support.HttpClientOgreProxy;
import com.marklogic.spring.batch.item.shapefile.support.OgreProxy;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class ShapefileProcessorTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

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

    //Todo - mock the httpClientOgreProxy (Requires internet!!)
    @Ignore
    @Test
    public void syriaCollegeShapefileProcessorTest() throws Exception {
        File shapefile = new ClassPathResource("./shapefiles/Colleges_Syria.zip").getFile();

        OgreProxy proxy = new HttpClientOgreProxy();
        ShapefileProcessor processor = new ShapefileProcessor(proxy);
        DocumentWriteOperation op = processor.process(shapefile);
        String jsonString = op.getContent().toString();
        logger.info(jsonString);

        assertTrue(jsonString.contains("College:Arab Academy for Science and Technologe and Maritime Transport"));

    }
}
