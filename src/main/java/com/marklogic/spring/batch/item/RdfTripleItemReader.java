package com.marklogic.spring.batch.item;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.CollectorStreamTriples;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

public class RdfTripleItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements InitializingBean{
	private static Log log = LogFactory.getLog(RdfTripleItemReader.class);
	public static final int VALUE_NOT_SET = -1;
	
	private int fetchSize = VALUE_NOT_SET;

	private int maxRows = VALUE_NOT_SET;

	private int queryTimeout = VALUE_NOT_SET;
	
	private boolean initialized = false;	
	
	private String fileName;
	
	protected Iterator<T> tripleIterator;
	
	private CollectorStreamTriples inputStream;
	
	public RdfTripleItemReader() {
		setName(ClassUtils.getShortName(RdfTripleItemReader.class));
	}	
	
	/**
	 * Assert that mandatory properties are set.
	 * 
	 * @throws IllegalArgumentException if fileName property not set.
	 */
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(fileName, "fileName must be provided");
		Assert.hasLength(fileName, "fileName must be provided");
	}
	
	/**
	 * Public setter for the file name for injection purposes.
	 * Specify the full path for the filename with valid separators
	 * 
	 * @param dataSource
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}	
	
	/**
	 * Uses RDFDataMgr to parse triples. 
	 */
	@SuppressWarnings("unchecked")
	private void parseTriples() {

		Assert.state(fileName.length() != 0, "FileName must not be null.");

		try {
			inputStream = new CollectorStreamTriples();
			Assert.state(inputStream != null, "inputStream must not be null");
			RDFDataMgr.parse(inputStream, fileName);
			log.info("Number of Triples [" + inputStream.getCollected().size() + "]");
			tripleIterator = (Iterator<T>) inputStream.getCollected().iterator();
			Assert.state(tripleIterator != null, "tripleIterator must not be null. Something went wrong");
		}
		catch (Exception e) {
			close();
			log.info("Caught exception[" + e.getMessage() + "]");
		}

	}	

	@Override
	protected void doClose() throws Exception {
		initialized = false;		
	}

	@Override
	protected void doOpen() throws Exception {
		Assert.state(!initialized, "Stream is already initialized.  Close before re-opening.");
		parseTriples();
		initialized = true;
	}

	@Override
	protected T doRead() throws Exception {
		if (tripleIterator == null) {
			throw new ReaderNotOpenException("Reader must be open before it can be read.");
		}

		try {
			if (!tripleIterator.hasNext()) {
				return null;
			}
			int currentRow = getCurrentItemCount();
			log.info("Current Row [" + currentRow + "]");
			@SuppressWarnings("unchecked")
			T item = (T) mapTripleRow(tripleIterator.next(), currentRow);
			return item;
		}
		catch (Exception e) {
			log.info("Caught exception[" + e.getMessage() + "]");
			throw new Exception("Attempt to process next row failed", e);
		}
	}	

	private Map<String, Object> mapTripleRow(T tripleRow, int rowNum)  {
		Map<String, Object> mapOfTriples = new LinkedCaseInsensitiveMap<Object>(1);
		String key = "triple-" + rowNum;
		mapOfTriples.put(key, tripleRow);
		return mapOfTriples;
	}	
}

