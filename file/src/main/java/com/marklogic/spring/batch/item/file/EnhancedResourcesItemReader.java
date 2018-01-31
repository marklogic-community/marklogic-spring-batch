package com.marklogic.spring.batch.item.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;

/**
 * Mimics mlcp in terms of what parameters are supported.
 */
public class EnhancedResourcesItemReader extends ResourcesItemReader {

    protected final static Logger logger = LoggerFactory.getLogger(EnhancedResourcesItemReader.class);

    private String inputFilePath;
    private String inputFilePattern;

    public EnhancedResourcesItemReader() {
        super();
    }

    public EnhancedResourcesItemReader(String inputFilePath, String inputFilePattern) {
        this.inputFilePath = inputFilePath;
        this.inputFilePattern = inputFilePattern;
    }

    /**
     * Initialize the array of resources based on inputFilePath.
     *
     * @param executionContext
     */
    @Override
    public void open(ExecutionContext executionContext) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String pattern = buildPattern();
        if (logger.isDebugEnabled()) {
            logger.debug("Finding resources with pattern: " + pattern);
        }

        try {
            setResources(resolver.getResources(pattern));
        } catch (IOException e) {
            throw new RuntimeException("Unable to resolve resources with pattern: " + pattern, e);
        }

        super.open(executionContext);
    }

    /**
     * Ask the superclass to read an item, and then if inputFilePattern has been set, verify that the
     * item's filename matches the pattern. If not, make another read.
     *
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Resource read() throws Exception {
        Resource r = super.read();
        if (r == null || inputFilePattern == null || r.getFilename().matches(inputFilePattern)) {
            return r;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Ignoring, doesn't match input file pattern: " + r.getFilename());
        }
        return read();
    }

    /**
     * Build a pattern to be used by Spring's PathMatchingResourcePatternResolver.
     *
     * @return
     */
    protected String buildPattern() {
        String pattern = inputFilePath;

        if (new File(inputFilePath).isDirectory()) {
            if (pattern.endsWith("/")) {
                pattern += "**";
            }
            else {
                pattern += "/**";
            }
        }

        if (!pattern.startsWith("classpath:") && !pattern.startsWith("file:")) {
            pattern = "file:" + pattern;
        }

        return pattern;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    public void setInputFilePattern(String inputFilePattern) {
        this.inputFilePattern = inputFilePattern;
    }
}
