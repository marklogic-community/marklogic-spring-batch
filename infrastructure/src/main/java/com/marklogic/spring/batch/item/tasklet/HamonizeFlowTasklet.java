package com.marklogic.spring.batch.item.tasklet;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.hub.FlowManager;
import com.marklogic.hub.HubConfig;
import com.marklogic.hub.flow.Flow;
import com.marklogic.hub.flow.FlowFinishedListener;
import com.marklogic.hub.flow.FlowItemCompleteListener;
import com.marklogic.hub.flow.FlowItemFailureListener;
import com.marklogic.hub.flow.FlowRunner;
import com.marklogic.hub.flow.FlowStatusListener;
import com.marklogic.hub.flow.FlowType;
import com.marklogic.hub.flow.RunFlowResponse;

public class HamonizeFlowTasklet implements Tasklet {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private DatabaseClient databaseClient;
	private FlowRunner flowRunner;
	private  Flow harmonizeFlow;
	private RunFlowResponse resp;
	private JobTicket jobTicket;
	
	
	public JobTicket getJobTicket() {
		return jobTicket;
	}

	public HamonizeFlowTasklet(DatabaseClient databaseClient, String hubPath, String hubEnv, String entityName,
			String harmonizeFlowName, Map<String, Object> options) {
		this.databaseClient = databaseClient;
		
		HubConfig dataHubConfig = HubConfig.hubFromEnvironment(hubPath, hubEnv);

		// get a flow manager
		FlowManager flowManager = new FlowManager(dataHubConfig);

		// retrieve the flow you wish to run
		harmonizeFlow = flowManager.getFlow(entityName, harmonizeFlowName, FlowType.HARMONIZE);

		// build the flow runner
		flowRunner = flowManager.newFlowRunner().withFlow(harmonizeFlow).withBatchSize(10).withThreadCount(4)
				.withOptions(options).withSourceClient(this.databaseClient)
				.withDestinationDatabase(dataHubConfig.finalDbName);
	}

	public void setFinishedListener(FlowFinishedListener listener) {
		flowRunner.onFinished(listener);
	}

	public void setItemCompleteListener(FlowItemCompleteListener listener) {
		this.flowRunner.onItemComplete(listener);
	}

	public void setItemFailedListener(FlowItemFailureListener listener) {
		this.flowRunner.onItemFailed(listener);
	}
	
	public void setStatusChangeListener(FlowStatusListener listener) {
		this.flowRunner.onStatusChanged(listener);
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		jobTicket = flowRunner.run();

		logger.info("Started Harmonization for " + this.harmonizeFlow.getEntityName() + ":"
				+ this.harmonizeFlow.getName() + " jobId=" + jobTicket.getJobId());
		// wait for flow to complete
		flowRunner.awaitCompletion();

		logger.info("Completed Harmonization for " + this.harmonizeFlow.getEntityName() + ":"
				+ this.harmonizeFlow.getName() + " jobId=" + jobTicket.getJobId());

		return RepeatStatus.FINISHED;
	}

}
