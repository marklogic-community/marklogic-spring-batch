package com.marklogic.uri;

import java.io.File;

import com.marklogic.client.helper.LoggingObject;

public class DefaultUriGenerator extends LoggingObject implements UriGenerator {

    private String replacementPairs;
    private String prefix;
    private String suffix;

    /**
     * Mirroring rules at https://docs.marklogic.com/guide/mlcp/import#id_31989.
     */
    @Override
    public String generateUri(Object o, String id) {
        String uri = generateInitialUri(o, id);
        if (prefix != null) {
            uri = prefix + uri;
        }
        if (suffix != null) {
            uri = uri + suffix;
        }
        return uri;
    }

    /**
     * For in-memory XML, which isn't supported by mlcp (everything comes from a file), we'll use the root element and a
     * counter.
     * 
     * @param o
     * @return
     */
    protected String generateInitialUri(Object o, String id) {
        if (o instanceof File) {
            return ((File) o).getAbsolutePath();
        }
        if (o instanceof String) {
            // TODO Assume XML for now
            String s = (String) o;
            if (s.startsWith("<")) {
                int pos = s.indexOf('>');
                String rootElementName = s.substring(1, pos);
                return "/" + rootElementName + "/" + id + ".xml";
            }
        }
        return o.toString();
    }

    public String getReplacementPairs() {
        return replacementPairs;
    }

    public void setReplacementPairs(String replacementPairs) {
        this.replacementPairs = replacementPairs;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

}
