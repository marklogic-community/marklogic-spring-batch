package com.marklogic.spring.batch.core.repository.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.marklogic.spring.batch.jdbc.support.incrementer.UriIncrementer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.NoSuchObjectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;

import org.springframework.batch.core.BatchStatus;
import com.marklogic.spring.batch.core.MarkLogicJobInstance;

@Component
public class MarkLogicJobExecutionDao extends AbstractMarkLogicBatchMetadataDao implements JobExecutionDao {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public MarkLogicJobExecutionDao(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
		this.incrementer = new UriIncrementer();
	}

	@Override
	public void saveJobExecution(JobExecution jobExecution) {
		validateJobExecution(jobExecution);
		
		jobExecution.incrementVersion();		
		jobExecution.setId(incrementer.nextLongValue());		
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
		String uri = SPRING_BATCH_DIR + jobExecution.getJobInstance().getId().toString() + ".xml";
        DocumentDescriptor desc = xmlDocMgr.exists(uri);
        JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<>(jaxbContext());
		xmlDocMgr.read(uri, handle);
		MarkLogicJobInstance mji = handle.get();
		mji.addJobExecution(jobExecution);
		//Set document metadata
        DocumentMetadataHandle jobInstanceMetadata = new DocumentMetadataHandle();
        jobInstanceMetadata.getCollections().add(COLLECTION_JOB_INSTANCE);
		xmlDocMgr.write(desc, jobInstanceMetadata, handle);
		logger.info("insert JobExecution:" + uri + "," + desc.getVersion());
	}
	
	/**
	 * Validate JobExecution. At a minimum, JobId, StartTime, EndTime, and
	 * Status cannot be null.
	 *
	 * @param jobExecution
	 * @throws IllegalArgumentException
	 */
	private void validateJobExecution(JobExecution jobExecution) {

		Assert.notNull(jobExecution);
		Assert.notNull(jobExecution.getJobId(), "JobExecution Job-Id cannot be null.");
		Assert.notNull(jobExecution.getStatus(), "JobExecution status cannot be null.");
		Assert.notNull(jobExecution.getCreateTime(), "JobExecution create time cannot be null");
	}

	@Override
	public void updateJobExecution(JobExecution jobExecution) {
		validateJobExecution(jobExecution);
		Assert.notNull(jobExecution.getId(),
				"JobExecution ID cannot be null. JobExecution must be saved before it can be updated");

		Assert.notNull(jobExecution.getVersion(),
				"JobExecution version cannot be null. JobExecution must be saved before it can be updated");
		
		JobExecution je = getJobExecution(jobExecution.getId());
		if (je == null) {
			throw new NoSuchObjectException("JobExecution " + jobExecution.getJobInstance().getJobName() + " " + jobExecution.getId() + " not found");
		}
		
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
        String uri = SPRING_BATCH_DIR + jobExecution.getJobInstance().getId().toString() + ".xml";
				
		synchronized (jobExecution) {
			DocumentDescriptor desc = xmlDocMgr.exists(uri);
			
			if (desc == null) {
				throw new NoSuchObjectException("Invalid JobExecution, Document " + uri + " not found.");
			}
			jobExecution.setVersion(jobExecution.getVersion() + 1);	
			JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<>(jaxbContext());
			xmlDocMgr.read(uri, handle);
			MarkLogicJobInstance mji = handle.get();
			mji.updateJobExecution(jobExecution);
	        //Set document metadata
	        DocumentMetadataHandle jobExecutionMetadata = new DocumentMetadataHandle();
	        jobExecutionMetadata.getCollections().add(COLLECTION_JOB_INSTANCE);
        	xmlDocMgr.write(desc, jobExecutionMetadata, handle);
			logger.info("update JobExecution:" + uri + "," + desc.getVersion());
		}
	}
	

	@Override
	public List<JobExecution> findJobExecutions(JobInstance jobInstance) {
		String uri = SPRING_BATCH_DIR + jobInstance.getId().toString() + ".xml";
    	XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
    	JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<>(jaxbContext());
    	MarkLogicJobInstance mji = xmlDocMgr.read(uri, handle).get();
    	List<JobExecution> jobExecutions = mji.getJobExecutions();
    	Collections.reverse(mji.getJobExecutions());
		return jobExecutions;
	}

	@Override
	public JobExecution getLastJobExecution(JobInstance jobInstance) {
		List<JobExecution> jobExecutions = findJobExecutions(jobInstance);
		if (jobExecutions.size() > 0) {
			return jobExecutions.get(0);
		} else {
			return null;
		}
	}

	@Override
	public Set<JobExecution> findRunningJobExecutions(String jobName) {
    	StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
    	StructuredQueryDefinition querydef = qb.and(qb.valueConstraint("jobName", jobName));
    	logger.info(querydef.serialize());
    	QueryManager queryMgr = databaseClient.newQueryManager();
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle()); 	    	
		Set<JobExecution> jobExecutions = new HashSet<>();
		for ( MatchDocumentSummary summary : results.getMatchResults() ) {
			JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<>(jaxbContext());
    		summary.getFirstSnippet(handle);
    		MarkLogicJobInstance mji = handle.get();
    		for (JobExecution je : mji.getJobExecutions()) {
    			if (je.getStatus().isRunning() && je.getEndTime() == null) {
    				jobExecutions.add(je);
    			}
    		}
    	}
    	return jobExecutions;
	}

	@Override
	public JobExecution getJobExecution(Long executionId) {
		JobExecution jobExec = null;
		StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef = qb.rangeConstraint("jobExecutionId", Operator.EQ, executionId.toString()); 
		QueryManager queryMgr = databaseClient.newQueryManager();
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle());
    	if (results.getTotalResults() > 0L) {
    		MatchDocumentSummary[] summaries = results.getMatchResults();
    		JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<>(jaxbContext());
    		MarkLogicJobInstance mji = summaries[0].getFirstSnippet(handle).get();	
    		if (mji.getJobExecutions().size() >= 1) {
    			for (JobExecution je : mji.getJobExecutions()) {
    				if (je.getId().equals(executionId)) { 
    					jobExec = je;
    				}
    			}			
    		} 
    	}
		return jobExec;
		
	}

	@Override
	public void synchronizeStatus(JobExecution jobExecution) {
		JobExecution je = getJobExecution(jobExecution.getId());
		int currentVersion = je.getVersion();

		if (currentVersion != jobExecution.getVersion()) {
			BatchStatus status = je.getStatus();
			jobExecution.upgradeStatus(status);
			jobExecution.setVersion(currentVersion);
		}
	}
	
	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext;
		try {
            jaxbContext = JAXBContext.newInstance(MarkLogicJobInstance.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
		return jaxbContext;
	}
}
