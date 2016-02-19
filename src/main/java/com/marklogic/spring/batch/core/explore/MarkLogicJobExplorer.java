package com.marklogic.spring.batch.core.explore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

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
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.MatchLocation;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawStructuredQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

public class MarkLogicJobExplorer implements JobExplorer {
	
	@Autowired
	private JAXBContext jaxbContext;	
	
	private static Logger logger = Logger.getLogger("com.marklogic.spring.batch.core.explore.MarkLogicJobExplorer");	
	
	private DatabaseClient client;
	private XMLDocumentManager docMgr;
	private QueryManager qryMgr;
	private JobExecutionAdapter jobExecutionAdapter; 
	
	public MarkLogicJobExplorer(DatabaseClient databaseClient) {
		this.client = databaseClient;
		jobExecutionAdapter = new JobExecutionAdapter();
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
		docMgr.read(MarkLogicSpringBatch.SPRING_BATCH_DIR + Long.toString(executionId), handle);
		
		JobExecution jobExecution = null;
		try {
			 jobExecution = jobExecutionAdapter.unmarshal(handle.get());
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}
		return jobExecution;
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
		List<JobExecution> jobExecutions = new ArrayList<JobExecution>();
		QueryOptionsManager optionsManager= client.newServerConfigManager().newQueryOptionsManager();
		String options = 
		"<options xmlns=\"http://marklogic.com/appservices/search\">" +
		  "<constraint name=\"jobInstance\">" +
		  	"<element-query name=\"jobInstance\" ns=\"http://projects.spring.io/spring-batch\" />" +
		  "</constraint>" +
		  "<constraint name=\"jobName\">" +
		  	"<value>" +
		  		"<element name=\"jobName\" ns=\"http://projects.spring.io/spring-batch\" />" +  
		  	"</value>" +
		  "</constraint>" +
		  "<transform-results apply=\"raw\" />" +
		"</options>";
		StringHandle writeHandle = new StringHandle(options);
		optionsManager.writeOptions("options", writeHandle);
		
		QueryManager queryMgr = client.newQueryManager();
		
		String query = 
				"<query xmlns=\"http://marklogic.com/appservices/search\">" + 
						"<container-constraint-query>" + 
							"<constraint-name>jobInstance</constraint-name>" + 
							"<value-constraint-query>" +
								"<constraint-name>jobName</constraint-name>" +
								"<text>sampleJob</text>" +
							"</value-constraint-query>" +
						"</container-constraint-query>" +
				"</query>";
		StringHandle rawHandle = new StringHandle(query);
		RawStructuredQueryDefinition querydef = queryMgr.newRawStructuredQueryDefinition(rawHandle, "options");

		SearchHandle results = queryMgr.search(querydef, new SearchHandle());
		MatchDocumentSummary[] summaries = results.getMatchResults();
		AdaptedJobExecution jobExec = null;
		for (MatchDocumentSummary summary : summaries ) {
			JAXBHandle<AdaptedJobExecution> jaxbHandle = new JAXBHandle<AdaptedJobExecution>(jaxbContext);
			summary.getFirstSnippet(jaxbHandle);
			jobExec = jaxbHandle.get();
			
		}
		JobExecutionAdapter adapter = new JobExecutionAdapter();
		try {
			jobExecutions.add(adapter.unmarshal(jobExec));
		} catch (Exception ex) {
			logger.severe(ex.getMessage());
		}
		
		return jobExecutions;
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
