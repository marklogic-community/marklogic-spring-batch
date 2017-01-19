package com.marklogic.spring.batch.item.reader;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.CollectorStreamTriples;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

/**
 * As part of the spring batch process we need an item reader and item writer
 * This class encapsulates and overrides the required methods for the item reader to process the data that 
 * has been read from the file into a map of the triple with a key identified by the row number.
 * @author viyengar
 *
 * @param <T> A map with a key and a triple object
 */
public class RdfTripleItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public static final int VALUE_NOT_SET = -1;

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
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}	
	
	/**
	 * Uses RDFDataMgr to parse triples by constructing the CollectorStreamTriples as the input stream 
	 */
	@SuppressWarnings("unchecked")
	private void parseTriples() {

		Assert.state(fileName.length() != 0, "FileName must not be null.");

		try {
			inputStream = new CollectorStreamTriples();
			Assert.state(inputStream != null, "inputStream must not be null");
			RDFDataMgr.parse(inputStream, fileName);
			logger.info("Number of Triples [" + inputStream.getCollected().size() + "]");
			tripleIterator = (Iterator<T>) inputStream.getCollected().iterator();
			Assert.state(tripleIterator != null, "tripleIterator must not be null. Something went wrong");
		}
		catch (Exception e) {
			close();
			logger.info("Caught exception[" + e.getMessage() + "]");
		}

	}	

	@Override
	protected void doClose() throws Exception {
		initialized = false;		
	}

	/**
	 * Once the stream is open for read, calls parseTriples to extract the triples and stuff the triples
	 * into tripleIterator. 
	 */
	@Override
	protected void doOpen() throws Exception {
		Assert.state(!initialized, "Stream is already initialized.  Close before re-opening.");
		parseTriples();
		initialized = true;
	}
	
	/**
	 * Once the reader is open, the method iterates through and extracts the triple that has been read
	 * into a map to process the item as part of the batch process. The size of the iterator depends
	 * on the chunk size of the spring batch
	 */
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
			logger.debug("Current Row [" + currentRow + "]");
			@SuppressWarnings("unchecked")
			T item = (T) mapTripleRow(tripleIterator.next(), currentRow);
			return item;
		}
		catch (Exception e) {
			logger.info("Caught exception[" + e.getMessage() + "]");
			throw new Exception("Attempt to process next row failed", e);
		}
	}	

	/**
	 * Constructs the map for triple based on the triple and the rowNum
	 * @param tripleRow
	 * @param rowNum
	 * @return the map with a key and the triple data
	 */
	private Map<String, Object> mapTripleRow(T tripleRow, int rowNum)  {
		Map<String, Object> mapOfTriples = new LinkedCaseInsensitiveMap<Object>(1);
		String key = "triple-" + rowNum;
		mapOfTriples.put(key, tripleRow);
		return mapOfTriples;
	}	
}

