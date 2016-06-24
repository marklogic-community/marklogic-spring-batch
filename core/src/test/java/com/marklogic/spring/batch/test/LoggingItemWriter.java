package com.marklogic.spring.batch.test;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.batch.item.ItemWriter;

public class LoggingItemWriter<T> implements ItemWriter<T> {

	Logger logger = Logger.getLogger("com.marklogic.spring.batch.test.LoggingItemWriter");

	@Override
	public void write(List<? extends T> items) throws Exception {
		for (T item : items) {
			logger.info(item.toString());
		}
		
	}
}
