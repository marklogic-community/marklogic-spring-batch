package com.marklogic.uri;

import java.util.UUID;

public class XmlStringUriGenerator implements UriGenerator<String> {

    @Override
    public String generateUri(String s, String id) {
        String rootDir = getRootDirectory(s);
        String path = "/" + rootDir + "/";
        return id != null ? path + id + ".xml" : path + UUID.randomUUID() + ".xml";
    }

    @Override
    public String generate() {
        return null;
    }

    protected String getRootDirectory(String s) {
        if (s.startsWith("<")) {
            int pos = s.indexOf('>');
            return s.substring(1, pos);
        } else {
            return s;
        }
    }
}
