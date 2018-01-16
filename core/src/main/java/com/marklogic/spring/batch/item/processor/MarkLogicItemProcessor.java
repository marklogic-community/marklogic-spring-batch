package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.document.DocumentWriteOperation;
import org.springframework.batch.item.ItemProcessor;

public interface MarkLogicItemProcessor<T> extends ItemProcessor<T, DocumentWriteOperation> {

}
