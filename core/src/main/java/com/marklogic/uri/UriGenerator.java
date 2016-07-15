package com.marklogic.uri;

public interface UriGenerator<T> {

    /**
     * @param t
     *            Object to generate a URI for - can be anything - a File, a String, etc.
     * @param id
     *            Optional ID to use in the URI; assumption is that if this is null, a UUID will be generated
     * @return
     */
    String generateUri(T t, String id);

    String generate();

}
