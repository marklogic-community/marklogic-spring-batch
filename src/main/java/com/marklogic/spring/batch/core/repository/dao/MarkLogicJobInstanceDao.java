package com.marklogic.spring.batch.core.repository.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobKeyGenerator;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.ValuesHandle;
import com.marklogic.client.query.CountedDistinctValue;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.client.query.ValuesDefinition;
import com.marklogic.spring.batch.bind.JobInstanceAdapter;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.repository.MarkLogicJobRepository;

public class MarkLogicJobInstanceDao extends AbstractMarkLogicBatchMetadataDao implements JobInstanceDao {
	
	private JobExecutionDao jobExecutionDao;
	
	private static final Log logger = LogFactory.getLog(MarkLogicJobInstanceDao.class);
	
	private JobKeyGenerator<JobParameters> jobKeyGenerator = new DefaultJobKeyGenerator();
	
	public MarkLogicJobInstanceDao(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
		validateJobInstanceParameters(jobName, jobParameters);

		Assert.state(getJobInstance(jobName, jobParameters) == null,
				"JobInstance must not already exist");
		
		JobInstance jobInstance = new JobInstance(incrementer.nextLongValue(), jobName);
		jobInstance.incrementVersion();
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
		String uri = SPRING_BATCH_INSTANCE_DIR + jobInstance.getId().toString() + ".xml";
        
		DocumentDescriptor desc = xmlDocMgr.exists(uri);
		if (desc == null) {
			desc = xmlDocMgr.newDescriptor(uri);
		}
		
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder;
        Document doc = null;
		try {
			documentBuilder = domFactory.newDocumentBuilder();
			doc = documentBuilder.newDocument();
	        Marshaller marshaller = jaxbContext().createMarshaller();
	        JobInstanceAdapter adapter = new JobInstanceAdapter();
	        AdaptedJobInstance aji  = adapter.marshal(jobInstance);
	        aji.setCreateDateTime(new Date(System.currentTimeMillis()));
	        aji.setJobParametersKey(jobKeyGenerator.generateKey(jobParameters));
	        marshaller.marshal(aji, doc);
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
        DocumentMetadataHandle jobInstanceMetadata = new DocumentMetadataHandle();
        jobInstanceMetadata.getCollections().add(COLLECTION_JOB_INSTANCE);
        
		xmlDocMgr.write(desc, jobInstanceMetadata, handle);
		logger.info("insert:" + uri + "," + desc.getVersion());
		
    	return jobInstance;
	}

	@Override
	public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
		validateJobInstanceParameters(jobName, jobParameters);
		
		StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
    	StructuredQueryDefinition querydef = 
    			qb.and(
    				qb.valueConstraint("jobParametersKey", jobKeyGenerator.generateKey(jobParameters)),
    				qb.valueConstraint("jobName", jobName),
    				qb.collection(COLLECTION_JOB_INSTANCE)
    			);
        QueryManager queryMgr = databaseClient.newQueryManager();
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle());
    	
    	List<JobInstance> jobInstances = new ArrayList<JobInstance>();
		MatchDocumentSummary[] summaries = results.getMatchResults();
		AdaptedJobInstance jobInstance = null;
		for (MatchDocumentSummary summary : summaries ) {
			JAXBHandle<AdaptedJobInstance> jaxbHandle = new JAXBHandle<AdaptedJobInstance>(jaxbContext());
			summary.getFirstSnippet(jaxbHandle);
			jobInstance = jaxbHandle.get();
			JobInstanceAdapter adapter = new JobInstanceAdapter();
			try {
				jobInstances.add(adapter.unmarshal(jobInstance));
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
		if (jobInstances.size() == 0) {
			return null;
		} else {
			return jobInstances.get(0);
		}
		
	}

	@Override
	public JobInstance getJobInstance(Long instanceId) {
		String uri = SPRING_BATCH_INSTANCE_DIR + instanceId.toString() + ".xml";
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
		DocumentDescriptor desc = xmlDocMgr.exists(uri);
		if (desc == null) {
			return null;
		} else {
			JAXBHandle<AdaptedJobInstance> jaxbHandle = xmlDocMgr.read(uri, new JAXBHandle<AdaptedJobInstance>(jaxbContext()));
			AdaptedJobInstance aji = jaxbHandle.get();
			JobInstanceAdapter adapter = new JobInstanceAdapter();
			JobInstance jobInstance = null;
			try {
				jobInstance = adapter.unmarshal(aji);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return jobInstance;
		}
		
	
	}

	@Override
	public JobInstance getJobInstance(JobExecution jobExecution) {
		Assert.notNull(jobExecution.getId());
		return jobExecutionDao.getJobExecution(jobExecution.getId()).getJobInstance();
	}

	@Override
	public List<JobInstance> getJobInstances(String jobName, int start, int count) {
		QueryManager queryMgr = databaseClient.newQueryManager();    	
    	StringQueryDefinition querydef = queryMgr.newStringDefinition(SEARCH_OPTIONS_NAME);
    	querydef.setCriteria("jobName: " + jobName + " AND type:job-instance AND sort:date");
    	logger.info(querydef.getCriteria());
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle()); 
    	List<JobInstance> jobInstances = new ArrayList<JobInstance>();
		MatchDocumentSummary[] summaries = results.getMatchResults();
		AdaptedJobInstance aji = null;
		if (start+count > summaries.length) {
			return jobInstances;
		}
		for (int i = start; i < start+count; i++) {
			JAXBHandle<AdaptedJobInstance> jaxbHandle = new JAXBHandle<AdaptedJobInstance>(jaxbContext());
			summaries[i].getFirstSnippet(jaxbHandle);
			aji = jaxbHandle.get();
			JobInstanceAdapter adapter = new JobInstanceAdapter();
			try {
				jobInstances.add(adapter.unmarshal(aji));
			} catch (Exception ex) {
				logger.error(ex.getMessage());
			}
		}
		return jobInstances;
	}

	@Override
	public List<String> getJobNames() {
		List<String> jobNames = new ArrayList<String>();
		QueryManager queryMgr = databaseClient.newQueryManager();
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("jobName", MarkLogicJobRepository.SEARCH_OPTIONS_NAME);
		ValuesHandle results = queryMgr.values(valuesDef, new ValuesHandle());
		for (CountedDistinctValue value : results.getValues()) {
			jobNames.add(value.get("xs:string", String.class));
		}
		return jobNames;
	}

	@Override
	public List<JobInstance> findJobInstancesByName(String jobName, int start, int count) {
    	return null;	
	}

	@Override
	public int getJobInstanceCount(String jobName) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return 0;
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

	public JobExecutionDao getJobExecutionDao() {
		return jobExecutionDao;
	}

	public void setJobExecutionDao(JobExecutionDao jobExecutionDao) {
		this.jobExecutionDao = jobExecutionDao;
	}
	
	private void validateJobInstanceParameters(String jobName, JobParameters jobParameters) {
		Assert.notNull(jobName, "Job name must not be null.");
		Assert.notNull(jobParameters, "JobParameters must not be null.");
	}



}
