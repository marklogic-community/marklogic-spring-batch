package com.marklogic.spring.batch.core.explore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.batch.runtime.BatchStatus;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.ValueQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.repository.MarkLogicJobRepository;

public class MarkLogicJobExplorer implements JobExplorer {	
	
	private static Logger logger = Logger.getLogger("com.marklogic.spring.batch.core.explore.MarkLogicJobExplorer");	
	
	private DatabaseClient client;
	private QueryManager queryMgr;
	
	public MarkLogicJobExplorer() {
		
	}
	
	public MarkLogicJobExplorer(DatabaseClient databaseClient) {
		this();
		this.client = databaseClient;
		queryMgr = client.newQueryManager();
	}

	@Override
	public List<JobInstance> getJobInstances(String jobName, int start, int count) {
		List<JobInstance> jobInstances = new ArrayList<JobInstance>();
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("jobInstanceId", MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		StructuredQueryBuilder qb = new StructuredQueryBuilder(MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		ValueQueryDefinition querydef = qb.and(qb.valueConstraint("jobName", jobName));	
		valuesDef.setQueryDefinition(querydef);
		ValuesHandle results = queryMgr.values(valuesDef, new ValuesHandle());
		for (int i = start; i < start + count; i++) {
			CountedDistinctValue value = results.getValues()[i];
			Long id = value.get("xs:unsignedLong", Long.class);
			JobInstance jobInstance = new JobInstance(id, jobName);
			jobInstances.add(jobInstance);
		}
		return jobInstances;
	}

	@Override
	public JobExecution getJobExecution(Long executionId) {
		StructuredQueryBuilder qb = new StructuredQueryBuilder(MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef = qb.and(
				qb.valueConstraint("jobExecutionId", executionId.toString())
			);	
		SearchHandle results = queryMgr.search(querydef, new SearchHandle());
		List<JobExecution> jobExecutions = getJobExecutionsFromSearchResults(results);
		assert(jobExecutions.size() == 1);
		return jobExecutions.get(0);
	}

	@Override
	public StepExecution getStepExecution(Long jobExecutionId, Long stepExecutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobInstance getJobInstance(Long instanceId) {
		StructuredQueryBuilder qb = new StructuredQueryBuilder(MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef = qb.and(
				qb.valueConstraint("jobInstanceId", instanceId.toString())
			);	
		SearchHandle results = queryMgr.search(querydef, new SearchHandle());
		List<JobExecution> jobExecutions = getJobExecutionsFromSearchResults(results);		
		return jobExecutions.get(0).getJobInstance();
	}

	@Override
	public List<JobExecution> getJobExecutions(JobInstance jobInstance) {
		StructuredQueryBuilder qb = new StructuredQueryBuilder(MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef = qb.and(
				qb.valueConstraint("jobInstanceId", jobInstance.getId().toString()),
				qb.valueConstraint("jobName", jobInstance.getJobName())
			);	
		logger.fine(querydef.serialize());
		SearchHandle results = queryMgr.search(querydef, new SearchHandle());
		return getJobExecutionsFromSearchResults(results);
	}

	@Override
	public Set<JobExecution> findRunningJobExecutions(String jobName) {
		StructuredQueryBuilder qb = new StructuredQueryBuilder(MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef = 
				qb.and(
						qb.valueConstraint("jobInstance", jobName),
						qb.not(
								qb.valueConstraint("status", BatchStatus.COMPLETED.toString(), BatchStatus.ABANDONED.toString(), BatchStatus.FAILED.toString(), BatchStatus.STOPPED.toString())
							)
					);
		SearchHandle results = queryMgr.search(querydef, new SearchHandle());
		Set<JobExecution> jobExecutions = new HashSet<JobExecution>(getJobExecutionsFromSearchResults(results));
		return jobExecutions;
	}

	@Override
	public List<String> getJobNames() {
		List<String> jobNames = new ArrayList<String>();
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("jobName", MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		ValuesHandle results = queryMgr.values(valuesDef, new ValuesHandle());
		for (CountedDistinctValue value : results.getValues()) {
			jobNames.add(value.get("xs:string", String.class));
		}
		return jobNames;
	}

	@Override
	public List<JobInstance> findJobInstancesByJobName(String jobName, int start, int count) {
		return getJobInstances(jobName, start, count);
	}

	@Override
	public int getJobInstanceCount(String jobName) throws NoSuchJobException {
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("jobInstanceId", MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		StructuredQueryBuilder qb = new StructuredQueryBuilder(MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		ValueQueryDefinition querydef = qb.and(qb.valueConstraint("jobName", jobName));	
		valuesDef.setQueryDefinition(querydef);
		ValuesHandle results = queryMgr.values(valuesDef, new ValuesHandle());
		int numberOfResults = results.getValues().length;
		if (numberOfResults == 0) {
			throw new NoSuchJobException(jobName);
		}
		return numberOfResults;
	}
	
	private List<JobExecution> getJobExecutionsFromSearchResults(SearchHandle results) {
		List<JobExecution> jobExecutions = new ArrayList<JobExecution>();
		MatchDocumentSummary[] summaries = results.getMatchResults();
		AdaptedJobExecution jobExec = null;
		for (MatchDocumentSummary summary : summaries ) {			
			JAXBHandle<AdaptedJobExecution> jaxbHandle = new JAXBHandle<AdaptedJobExecution>(jaxbContext());
			summary.getFirstSnippet(jaxbHandle);
			jobExec = jaxbHandle.get();
			JobExecutionAdapter adapter = new JobExecutionAdapter();
			try {
				jobExecutions.add(adapter.unmarshal(jobExec));
			} catch (Exception ex) {
				logger.severe(ex.getMessage());
			}
		}
		return jobExecutions;
	}
	
	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext = null;
		try {
            jaxbContext = JAXBContext.newInstance(AdaptedJobExecution.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
		return jaxbContext;
	}
	
	

}
