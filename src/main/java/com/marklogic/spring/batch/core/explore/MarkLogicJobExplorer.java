package com.marklogic.spring.batch.core.explore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.factory.annotation.Autowired;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

public class MarkLogicJobExplorer implements JobExplorer {
	
	@Autowired
	private JAXBContext jaxbContext;	
	
	private DatabaseClient client;
	private XMLDocumentManager docMgr;
	private QueryManager qryMgr;
	
	public MarkLogicJobExplorer(DatabaseClient databaseClient) {
		this.client = databaseClient;
		docMgr = client.newXMLDocumentManager();
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
		JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<AdaptedJobExecution>(jaxbContext);
		docMgr.read(MarkLogicSpringBatch.SPRING_BATCH_DIR + "/" + Long.toString(executionId));
		return handle.get().getJobExecution();
	}

	@Override
	public StepExecution getStepExecution(Long jobExecutionId, Long stepExecutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobInstance getJobInstance(Long instanceId) {
		DOMHandle handle = docMgr.read(MarkLogicSpringBatch.SPRING_BATCH_DIR + "/job-instance/" + instanceId.toString(), new DOMHandle());
		JobInstance jobInstance = null;
		try {
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			jobInstance = (JobInstance)unmarshaller.unmarshal(handle.get());
		} catch (JAXBException ex) {
			ex.printStackTrace();
		}
		return jobInstance;
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
