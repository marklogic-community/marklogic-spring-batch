package com.marklogic.client.spring.batch.corb;

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

public class GetUrisTasklet implements Tasklet, InitializingBean {
	
	@Autowired
	private DatabaseClientProvider databaseClientProvider;

	@Override
	public void afterPropertiesSet() throws Exception {
		
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		DatabaseClient client = databaseClientProvider.getDatabaseClient();
		ServerEvaluationCall callUrisModule = client.newServerEval();
		callUrisModule.xquery("cts:uris()");
		callUrisModule.eval();
		EvalResultIterator result = callUrisModule.eval();
		while (result.hasNext()) {
			String uri = result.next().getString();
			System.out.println(uri);
		}	
		return RepeatStatus.FINISHED;
	}

}
