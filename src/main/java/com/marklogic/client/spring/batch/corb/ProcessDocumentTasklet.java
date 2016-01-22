package com.marklogic.client.spring.batch.corb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.batch.geonames.GeonamesConfig;

public class ProcessDocumentTasklet implements Tasklet, InitializingBean {
	
	private Log log = LogFactory.getLog(ProcessDocumentTasklet.class);
	
	private DatabaseClient databaseClient;
	
	public ProcessDocumentTasklet(DatabaseClientProvider databaseClientProvider) {
		databaseClient = databaseClientProvider.getDatabaseClient();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		/*
		ServerEvaluationCall processDocument = databaseClient.newServerEval();
		processDocument.xquery("()");
		processDocument.eval();
		EvalResultIterator result = processDocument.eval();
		while (result.hasNext()) {
			String uri = result.next().getString();
			System.out.println(uri);
		}
		*/
		log.info("processDocument");
		return RepeatStatus.FINISHED;
	}
	
}