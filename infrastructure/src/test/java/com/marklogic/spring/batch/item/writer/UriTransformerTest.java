package com.marklogic.spring.batch.item.writer;

import com.marklogic.spring.batch.item.writer.support.DefaultUriTransformer;
import com.marklogic.spring.batch.item.writer.support.UriTransformer;
import org.junit.Assert;
import org.junit.Test;

public class UriTransformerTest extends Assert {

    @Test
    public void uriPrefixWithNullValuesTest() {
        UriTransformer transformer = new DefaultUriTransformer("pre", null, null);
        String uri = "test";
        assertEquals("pretest", transformer.transform(uri));
    }

    @Test
    public void uriPrefixWithEmptyStringValuesTest() {
        UriTransformer transformer = new DefaultUriTransformer("pre", "", "");
        String uri = "test";
        assertEquals("pretest", transformer.transform(uri));
    }

    @Test
    public void uriSuffixTest() {
        UriTransformer transformer = new DefaultUriTransformer("", "post", "");
        String uri = "test";
        assertEquals("testpost", transformer.transform(uri));
    }

    @Test
    public void uriReplaceTest() {
        UriTransformer transformer = new DefaultUriTransformer("", "", "test,ABC");
        String uri = "thisisatest";
        assertEquals("thisisaABC", transformer.transform(uri));
    }

    @Test
    public void uriPrefixAndReplaceTest() {
        UriTransformer transformer = new DefaultUriTransformer("pre", "", "test,ABC");
        String uri = "thisisatest";
        assertEquals("prethisisaABC", transformer.transform(uri));
    }
}
