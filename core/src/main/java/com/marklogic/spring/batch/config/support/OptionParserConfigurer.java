package com.marklogic.spring.batch.config.support;

import joptsimple.OptionParser;

/**
 * Typically implemented by a Configuration class so that it can reveal its recognized command line
 * options when a client asks for "help".
 */
public interface OptionParserConfigurer {

    void configureOptionParser(OptionParser parser);
}
