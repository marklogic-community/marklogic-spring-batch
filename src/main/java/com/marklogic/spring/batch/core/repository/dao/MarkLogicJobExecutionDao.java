package com.marklogic.spring.batch.core.repository.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

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
		String uri = SPRING_BATCH_DIR + jobExecution.getId().toString() + ".xml";
        DocumentDescriptor desc = xmlDocMgr.newDescriptor(uri);
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder;
        Document doc = null;
		try {
			documentBuilder = domFactory.newDocumentBuilder();
			doc = documentBuilder.newDocument();
	        Marshaller marshaller = jaxbContext().createMarshaller();
	        JobExecutionAdapter adapter = new JobExecutionAdapter();
	        AdaptedJobExecution aje  = adapter.marshal(jobExecution);;
	        marshaller.marshal(aje, doc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        DOMHandle handle = new DOMHandle();
        handle.set(doc);      
      
        //Set document metadata
        DocumentMetadataHandle jobExecutionMetadata = new DocumentMetadataHandle();
        jobExecutionMetadata.getCollections().add(MarkLogicSpringBatch.COLLECTION_JOB_EXECUTION);
        
		xmlDocMgr.write(desc, jobExecutionMetadata, handle);
		logger.info("insert:" + uri + "," + desc.getVersion());
            
			
            
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
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
        String uri = SPRING_BATCH_DIR + jobExecution.getId().toString() + ".xml";
		
		
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
			try {
				documentBuilder = domFactory.newDocumentBuilder();
				doc = documentBuilder.newDocument();
		        Marshaller marshaller = jaxbContext().createMarshaller();
		        JobExecutionAdapter adapter = new JobExecutionAdapter();
		        AdaptedJobExecution aje  = adapter.marshal(jobExecution);
		        marshaller.marshal(aje, doc);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (JAXBException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	        DOMHandle handle = new DOMHandle();
	        handle.set(doc);      
	      
	        //Set document metadata
	        DocumentMetadataHandle jobExecutionMetadata = new DocumentMetadataHandle();
	        jobExecutionMetadata.getCollections().add(MarkLogicSpringBatch.COLLECTION_JOB_EXECUTION);
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
    				qb.collection(COLLECTION_JOB_EXECUTION)
    			);
		return findJobExecutions(querydef);
		
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
    	StructuredQueryDefinition querydef = 
    			qb.and(
    				qb.valueConstraint("status", BatchStatus.STARTED.toString(), BatchStatus.STARTING.toString()),
    				qb.valueConstraint("jobName", jobName),
    				qb.not(qb.containerConstraint("endDateTime", qb.and()))
    			);
    	Set<JobExecution> jobExecutions = new HashSet<JobExecution>();
    	for ( JobExecution jobExecution : findJobExecutions(querydef) ) {
    		jobExecutions.add(jobExecution);
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
            jaxbContext = JAXBContext.newInstance(AdaptedJobExecution.class, AdaptedJobInstance.class, AdaptedJobParameters.class, AdaptedStepExecution.class);
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
