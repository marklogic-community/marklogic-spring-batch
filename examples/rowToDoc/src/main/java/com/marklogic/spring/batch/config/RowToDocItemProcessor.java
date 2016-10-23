package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

public class RowToDocItemProcessor implements ItemProcessor<Map<String, Object>, MarkLogicItemWriter> {

    @Override
    public MarkLogicItemWriter process(Map<String, Object> item) throws Exception {
        return null;
    }
}
