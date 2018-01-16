package com.marklogic.spring.batch.item.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

public class SimpleChunkListener implements ChunkListener {

    Logger logger = LoggerFactory.getLogger(SimpleChunkListener.class);

    @Override
    public void beforeChunk(ChunkContext context) {

    }

    @Override
    public void afterChunk(ChunkContext context) {
        logger.info("Chunk Processed");
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
