package com.marklogic.spring.batch.item.file.support;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class TikaParserTest {

    private Resource wordDocument;
    private String parsedXml;

    @Test
    public void parseWordDocumentTest() throws Exception {
        givenWordDocument("word/test-1.docx");
        whenDocumentIsParsed();
        thenContainsText();
    }

    public void givenWordDocument(String path) {
        wordDocument = new ClassPathResource(path);
    }

    public void whenDocumentIsParsed() throws Exception {
        parsedXml = TikaParser.parseToXML(wordDocument.getInputStream());
    }

    public void thenContainsText() {
        assertThat(parsedXml, containsString("The quick brown fox"));
    }
}
