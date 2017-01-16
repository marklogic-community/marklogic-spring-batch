package com.marklogic.spring.batch.item.writer;

import org.junit.Assert;
import org.junit.Test;

public class UriTransformerTest extends Assert {

    @Test
    public void uriPrefixWithNullValuesTest() {
        UriTransformer transformer = new UriTransformer("pre", null, null);
        String uri = "test";
        assertEquals("pretest", transformer.transform(uri));
    }

    @Test
    public void uriPrefixWithEmptyStringValuesTest() {
        UriTransformer transformer = new UriTransformer("pre", "", "");
        String uri = "test";
        assertEquals("pretest", transformer.transform(uri));
    }
}
