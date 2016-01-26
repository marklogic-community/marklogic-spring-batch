package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.helper.DatabaseClientProvider;

public class MarkLogicItemReader<T> implements ItemReader<T> {

	private DatabaseClient databaseClient;
	
	String urisModule;
	
	EvalResultIterator resultIterator;
	
	private Log log = LogFactory.getLog(MarkLogicItemReader.class);
	
	public MarkLogicItemReader(DatabaseClientProvider databaseClientProvider) {
		log.info("ML ITEM READER");
		databaseClient = databaseClientProvider.getDatabaseClient();	
		ServerEvaluationCall callUrisModule = databaseClient.newServerEval();
		callUrisModule.modulePath(urisModule);
		resultIterator = callUrisModule.eval();
	}

	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	public void setDatabaseClient(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}
	
	public String getUrisModule() {
		return urisModule;
	}

	public void setUrisModule(final String uris) {
		this.urisModule = uris;
	}
	
	@Override
	@SuppressWarnings({ "unchecked" })
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		T result = null;
		if (resultIterator.hasNext()) {
			EvalResult item = resultIterator.next();
			switch (item.getType()) {
				case INTEGER: {  
					result = (T) item.getAs(Integer.class);
					break;
				}
				case STRING: {
					result = (T) item.getAs(String.class);
					break;
				}
				default: {
					break;
				}
			}
		} 
		return result;
	}

}
