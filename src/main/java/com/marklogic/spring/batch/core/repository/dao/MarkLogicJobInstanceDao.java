package com.marklogic.spring.batch.core.repository.dao;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.core.AdaptedJobExecution;

public class MarkLogicJobInstanceDao extends AbstractMarkLogicBatchMetadataDao implements JobInstanceDao {
	
	private static final Log logger = LogFactory.getLog(MarkLogicJobInstanceDao.class);
	
	public MarkLogicJobInstanceDao(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
		Assert.notNull(jobName, "Job name must not be null.");
		Assert.notNull(jobParameters, "JobParameters must not be null.");

		Assert.state(getJobInstance(jobName, jobParameters) == null,
				"JobInstance must not already exist");
		
		JobInstance jobInstance = new JobInstance(incrementer.nextLongValue(), jobName);
    	return jobInstance;
	}

	@Override
	public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobInstance getJobInstance(Long instanceId) {
		StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef = qb.and(
				qb.valueConstraint("jobInstanceId", instanceId.toString())
			);	
		List<JobExecution> jobExecutions = findJobExecutions(querydef);		
		if (jobExecutions.size() == 1) {
			return jobExecutions.get(0).getJobInstance();
		} else {
			return null;
		}
	}

	@Override
	public JobInstance getJobInstance(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobInstance> getJobInstances(String jobName, int start, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getJobNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobInstance> findJobInstancesByName(String jobName, int start, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getJobInstanceCount(String jobName) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private List<JobExecution> findJobExecutions(StructuredQueryDefinition querydef) {
    	logger.info(querydef.serialize());
    	QueryManager queryMgr = databaseClient.newQueryManager();
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle()); 	
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
				logger.error(ex.getMessage());
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
