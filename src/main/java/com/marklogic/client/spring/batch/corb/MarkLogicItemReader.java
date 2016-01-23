package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.helper.DatabaseClientProvider;

public class MarkLogicItemReader<T> implements ItemReader<T> {
	
	private DatabaseClient databaseClient;
	private String urisModule;
	EvalResultIterator resultIterator;
	
	private Log log = LogFactory.getLog(MarkLogicItemReader.class);
	
	public MarkLogicItemReader(DatabaseClientProvider databaseClientProvider, String urisModule) {
		log.info("ML ITEM READER");
		databaseClient = databaseClientProvider.getDatabaseClient();	
		setUrisModule(urisModule);
		ServerEvaluationCall callUrisModule = databaseClient.newServerEval();
		callUrisModule.xquery("cts:uris()");
		callUrisModule.eval();
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

	public void setUrisModule(String urisModule) {
		this.urisModule = urisModule;
	}


	@Override
	public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		if (resultIterator.hasNext()) 
			return (T) resultIterator.next().getAs(String.class);
		else
			return null;
	}

}
