package com.marklogic.spring.batch.core.repository.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.NoSuchObjectException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

import org.springframework.batch.core.BatchStatus;
import com.marklogic.spring.batch.core.MarkLogicJobInstance;

public class MarkLogicJobExecutionDao extends AbstractMarkLogicBatchMetadataDao implements JobExecutionDao {
	
	private static final Log logger = LogFactory.getLog(MarkLogicJobExecutionDao.class);
	
	public MarkLogicJobExecutionDao() {
	}
	
	private JobInstanceDao jobInstanceDao;
	
	public MarkLogicJobExecutionDao(DatabaseClient databaseClient, JobInstanceDao jobInstanceDao) {
		this.databaseClient = databaseClient;
		this.jobInstanceDao = jobInstanceDao;
	}

	@Override
	public void saveJobExecution(JobExecution jobExecution) {
		validateJobExecution(jobExecution);
		
		jobExecution.incrementVersion();		
		jobExecution.setId(incrementer.nextLongValue());		
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
		String uri = SPRING_BATCH_DIR + jobExecution.getJobInstance().getId().toString() + ".xml";
        DocumentDescriptor desc = xmlDocMgr.exists(uri);
        JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<MarkLogicJobInstance>(jaxbContext());
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
		
		JobInstance jobInstance = jobInstanceDao.getJobInstance(jobExecution);
		Assert.notNull(jobInstance);
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
        String uri = SPRING_BATCH_DIR + jobExecution.getJobInstance().getId().toString() + ".xml";
				
		synchronized (jobExecution) {
			DocumentDescriptor desc = xmlDocMgr.exists(uri);
			
			if (desc == null) {
				throw new NoSuchObjectException("Invalid JobExecution, Document " + uri + " not found.");
			}
			jobExecution.setVersion(jobExecution.getVersion() + 1);	
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	        domFactory.setNamespaceAware(true);
	        DocumentBuilder documentBuilder;
	        Document doc = null;
	        DOMHandle handle = new DOMHandle();
	        handle.set(doc);      
	      
	        //Set document metadata
	        DocumentMetadataHandle jobExecutionMetadata = new DocumentMetadataHandle();
	        jobExecutionMetadata.getCollections().add(COLLECTION_JOB_INSTANCE);
	        try {
	        	xmlDocMgr.write(desc, jobExecutionMetadata, handle);
				logger.info("update:" + uri + "," + desc.getVersion());
	        } catch (FailedRequestException ex) {
	        	logger.error(ex.getMessage());
	        	throw new OptimisticLockingFailureException(ex.getMessage());
	        } catch (Exception ex) {
	        	logger.error(ex.getMessage());
	        	ex.printStackTrace();
	        }
			
		}
	}
	

	@Override
	public List<JobExecution> findJobExecutions(JobInstance jobInstance) {
    	StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
    	StructuredQueryDefinition querydef = 
    			qb.and(
    				qb.valueConstraint("jobInstanceId", jobInstance.getId().toString()), 
    				qb.valueConstraint("jobName", jobInstance.getJobName()),
    				qb.collection(COLLECTION_JOB_INSTANCE)
    			);
		return findJobExecutions(querydef);
		
	}

	@Override
	public JobExecution getLastJobExecution(JobInstance jobInstance) {
		List<JobExecution> jobExecutions = findJobExecutions(jobInstance);
		if (jobExecutions.size() > 0) {
			return jobExecutions.get(jobExecutions.size() - 1);
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
		Set<JobExecution> jobExecutions = new HashSet<JobExecution>();
		for ( MatchDocumentSummary summary : results.getMatchResults() ) {
			JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<MarkLogicJobInstance>(jaxbContext());
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
		StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef = qb.and(
				qb.valueConstraint("jobExecutionId", executionId.toString())
			);	
		List<JobExecution> jobExecutions = findJobExecutions(querydef);
		if (jobExecutions.size() == 1) {
			return jobExecutions.get(0);
		} else {
			return null;
		}
		
	}

	@Override
	public void synchronizeStatus(JobExecution jobExecution) {
		JobExecution je = getJobExecution(jobExecution.getId());
		int currentVersion = je.getVersion().intValue();

		if (currentVersion != jobExecution.getVersion().intValue()) {
			BatchStatus status = je.getStatus();
			jobExecution.upgradeStatus(status);
			jobExecution.setVersion(currentVersion);
		}
	}
	
	private List<JobExecution> findJobExecutions(StructuredQueryDefinition querydef) {
    	logger.info(querydef.serialize());
    	QueryManager queryMgr = databaseClient.newQueryManager();
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle()); 	
		MatchDocumentSummary[] summaries = results.getMatchResults();
		JAXBHandle<MarkLogicJobInstance> handle = new JAXBHandle<MarkLogicJobInstance>(jaxbContext());
		summaries[0].getFirstSnippet(handle);
		return handle.get().getJobExecutions();	
	}
	
	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext = null;
		try {
            jaxbContext = JAXBContext.newInstance(MarkLogicJobInstance.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
		return jaxbContext;
	}

	public JobInstanceDao getJobInstanceDao() {
		return jobInstanceDao;
	}

	public void setJobInstanceDao(JobInstanceDao jobInstanceDao) {
		this.jobInstanceDao = jobInstanceDao;
	}

}
