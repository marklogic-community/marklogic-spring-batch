package com.marklogic.client.spring.batch.corb;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.helper.DatabaseClientProvider;

public class MarkLogicItemWriter<T> implements ItemWriter<T> {
	
	private DatabaseClient databaseClient;
	private String processModule;
	private Log log = LogFactory.getLog(MarkLogicItemWriter.class);

	public void setProcessModule(String process) {
		this.processModule = process;
	}
	
	public MarkLogicItemWriter(DatabaseClientProvider databaseClientProvider, String processModule) {
		log.info("ML Item Writer");
		databaseClient = databaseClientProvider.getDatabaseClient();
		setProcessModule(processModule);
	}

	@Override
	public void write(List<? extends T> items) throws Exception {
		for (T uri : items) {
			ServerEvaluationCall invokeProcessModule = databaseClient.newServerEval();
			invokeProcessModule.modulePath(processModule);
			invokeProcessModule.addVariable("URI", uri.toString());
			System.out.println(invokeProcessModule.evalAs(String.class));
		}
	}

	public String getProcessModule() {
		return processModule;
	}

}
