package com.marklogic.client.spring.batch.core.explore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

public class MarkLogicJobExplorer implements JobExplorer {
	
	private DatabaseClient client;
	private XMLDocumentManager xmlDocMgr;
	private QueryManager qryMgr;
	
	public MarkLogicJobExplorer(DatabaseClient databaseClient) {
		client = databaseClient;
		xmlDocMgr = client.newXMLDocumentManager();
		qryMgr = client.newQueryManager();
	}

	@Override
	public List<JobInstance> getJobInstances(String jobName, int start, int count) {
		List<JobInstance> jobInstances = new ArrayList<JobInstance>();
		StructuredQueryBuilder sb = qryMgr.newStructuredQueryBuilder("myopt");

		// put code from examples here
		StructuredQueryDefinition criteria = sb.collection("http://marklogic.com/spring-batch/job-instance");

		StringHandle searchHandle = qryMgr.search(criteria, new StringHandle());
		System.out.println(searchHandle.get());
		return jobInstances;
	}

	@Override
	public JobExecution getJobExecution(Long executionId) {
		StructuredQueryBuilder sb = qryMgr.newStructuredQueryBuilder();

		// put code from examples here
		StructuredQueryDefinition criteria = sb.document("http://marklogic.com/spring-batch/job-execution");

		StringHandle searchHandle = qryMgr.search(criteria, new StringHandle());
		System.out.println(searchHandle.get());
		return jobExecution;
	}

	@Override
	public StepExecution getStepExecution(Long jobExecutionId, Long stepExecutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobInstance getJobInstance(Long instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobExecution> getJobExecutions(JobInstance jobInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<JobExecution> findRunningJobExecutions(String jobName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getJobNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobInstance> findJobInstancesByJobName(String jobName, int start, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getJobInstanceCount(String jobName) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return 0;
	}

}
