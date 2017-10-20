package com.marklogic.spring.batch.item.processor.support;

public interface UriGenerator<T> {

    /**
     * @param t
     *            Object to generate a URI for - can be anything - a File, a String, etc.
     *
     * @return
     */
    public String generateUri(T t) throws Exception;

}
