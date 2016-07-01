package com.marklogic.uri;

import java.io.File;

public class DefaultUriGenerator implements UriGenerator<String>{

    public String getOutputUriPrefix() {
        return outputUriPrefix;
    }

    public void setOutputUriPrefix(String outputUriPrefix) {
        this.outputUriPrefix = outputUriPrefix;
    }

    public String getOutputUriSuffix() {
        return outputUriSuffix;
    }

    public void setOutputUriSuffix(String outputUriSuffix) {
        this.outputUriSuffix = outputUriSuffix;
    }

    public String getOutputUriReplace() {
        return outputUriReplace;
    }

    public void setOutputUriReplace(String outputUriReplace) {
        this.outputUriReplace = outputUriReplace;
    }

    private String outputUriPrefix;

    private String outputUriReplace;

    private String outputUriSuffix;

    private String applyOutputUriReplace(String uri, String outputUriReplace) {
        String[] regexReplace = outputUriReplace.split(",");
        for (int i = 0; i < regexReplace.length; i=i+2) {
            String regex = regexReplace[i];
            String replace = regexReplace[i+1];
            uri = uri.replaceAll(regex, replace);
        }
        return uri;
    }

    @Override
    public String generateUri(String s, String id) {
        String uri = s;
        uri = (getOutputUriReplace() != null) ? applyOutputUriReplace(uri, getOutputUriReplace()) : uri;
        uri = (getOutputUriPrefix() != null) ? getOutputUriPrefix() + uri : uri;
        uri = (getOutputUriSuffix() != null) ? uri + getOutputUriSuffix() : uri;
        return uri;
    }

    @Override
    public String generate() {
        return null;
    }
}
