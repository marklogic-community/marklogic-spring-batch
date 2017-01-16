package com.marklogic.spring.batch.item.writer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class UriTransformerTest extends Assert {

    @Test
    public void uriPrefixTest() {
        UriTransformer transformer = new UriTransformer("pre", null, null);
        String uri = "test";
        assertEquals("pretest", transformer.transform(uri));
    }
}
