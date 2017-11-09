package com.marklogic.spring.batch.item.writer.support;

public class DefaultUriTransformer implements UriTransformer {

    private String outputUriPrefix;
    private String outputUriReplace;
    private String outputUriSuffix;

    public DefaultUriTransformer() {

    }

    public DefaultUriTransformer (String outputUriPrefix, String outputUriSuffix, String outputUriReplace) {
        this.outputUriPrefix = outputUriPrefix;
        this.outputUriSuffix = outputUriSuffix;
        this.outputUriReplace = outputUriReplace;
    }

    @Override
    public String transform(String uri) {
        uri = (outputUriReplace != null) && !outputUriReplace.isEmpty() ? applyOutputUriReplace(uri, outputUriReplace) : uri;
        uri = (outputUriPrefix != null) && !outputUriPrefix.isEmpty() ? outputUriPrefix + uri : uri;
        uri = (outputUriSuffix != null) && !outputUriSuffix.isEmpty() ? uri + outputUriSuffix : uri;
        return uri;
    }

    private String applyOutputUriReplace(String uri, String outputUriReplace) {
        String[] regexReplace = outputUriReplace.split(",");
        for (int i = 0; i < regexReplace.length; i=i+2) {
            String regex = regexReplace[i];
            String replace = regexReplace[i+1];
            uri = uri.replaceAll(regex, replace);
        }
        return uri;
    }

    /*
    Prepends a string to the URI after substitution
     */
    public void setOutputUriPrefix(String outputUriPrefix) {
        this.outputUriPrefix = outputUriPrefix;
    }

    /*
    The outputUriReplace option accepts a comma delimited list of regular expression and replacement string pairs
    @param outputUriReplace Example regex,'replaceString',regex,'replaceString'
     */
    public void setOutputUriReplace(String outputUriReplace) {
        this.outputUriReplace = outputUriReplace;
    }

    /*
    Appends a string to the URI after substitution
     */
    public void setOutputUriSuffix(String outputUriSuffix) {
        this.outputUriSuffix = outputUriSuffix;
    }
}
