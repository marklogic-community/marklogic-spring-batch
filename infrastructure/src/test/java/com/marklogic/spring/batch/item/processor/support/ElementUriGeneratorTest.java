package com.marklogic.spring.batch.item.processor.support;

import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;

public class ElementUriGeneratorTest {

    @Test
    public void generateIdFromDocumentTest() throws Exception {
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document document = jdomBuilder.build(new StringReader("<xml><id>ABC</id><notID>123</notID></xml>"));

        ElementUriGenerator uriGenerator = new ElementUriGenerator("id");
        assertThat("Expecting ABC is generated URI", uriGenerator.generateUri(document).equals("ABC"));

    }

    @Test
    public void generateIdFromDocumentWithNamespaceTest() throws Exception {
        SAXBuilder jdomBuilder = new SAXBuilder();
        Document document = jdomBuilder.build(new StringReader("<xml xmlns=\"abc\"><id>ABC</id><notID>123</notID></xml>"));

        ElementUriGenerator uriGenerator = new ElementUriGenerator("abc","id");
    }
}
