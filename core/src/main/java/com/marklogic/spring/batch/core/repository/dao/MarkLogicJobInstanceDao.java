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

import com.marklogic.client.query.*;
import com.marklogic.spring.batch.jdbc.support.incrementer.UriIncrementer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobKeyGenerator;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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
import com.marklogic.spring.batch.core.MarkLogicJobInstance;

@Component
public class MarkLogicJobInstanceDao extends AbstractMarkLogicBatchMetadataDao implements JobInstanceDao {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private JobKeyGenerator<JobParameters> jobKeyGenerator = new DefaultJobKeyGenerator();
	
	@Autowired
	public MarkLogicJobInstanceDao(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
		this.incrementer = new UriIncrementer();
	}

	@Override
	public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
		validateJobInstanceParameters(jobName, jobParameters);

		Assert.state(getJobInstance(jobName, jobParameters) == null,
				"JobInstance must not already exist");
		
		JobInstance jobInstance = new JobInstance(incrementer.nextLongValue(), jobName);
		jobInstance.incrementVersion();
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
		String uri = SPRING_BATCH_DIR + jobInstance.getId().toString() + ".xml";
        
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
	        MarkLogicJobInstance mji = new MarkLogicJobInstance(jobInstance);
	        mji.setJobKey(jobKeyGenerator.generateKey(jobParameters));
	        mji.setCreatedDateTime(new Date(System.currentTimeMillis()));
	        marshaller.marshal(mji, doc);
		} catch (ParserConfigurationException | JAXBException e) {
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
		logger.debug("insert:" + uri + "," + desc.getVersion());
		
    	return jobInstance;
	}

	@Override
	public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
		validateJobInstanceParameters(jobName, jobParameters);
		
		StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
    	StructuredQueryDefinition querydef = 
    			qb.and(
    				qb.valueConstraint("jobKey", jobKeyGenerator.generateKey(jobParameters)),
    				qb.valueConstraint("jobName", jobName),
    				qb.collection(COLLECTION_JOB_INSTANCE)
    			);
        QueryManager queryMgr = databaseClient.newQueryManager();
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle());
    	
    	List<JobInstance> jobInstances = new ArrayList<>();
		MatchDocumentSummary[] summaries = results.getMatchResults();
		MarkLogicJobInstance jobInstance;
		for (MatchDocumentSummary summary : summaries ) {
			JAXBHandle<MarkLogicJobInstance> jaxbHandle = new JAXBHandle<>(jaxbContext());
			summary.getFirstSnippet(jaxbHandle);
			jobInstance = jaxbHandle.get();
			try {
				jobInstances.add(jobInstance.getJobInstance());
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
		String uri = SPRING_BATCH_DIR + instanceId.toString() + ".xml";
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
		DocumentDescriptor desc = xmlDocMgr.exists(uri);
		if (desc == null) {
			return null;
		} else {
			JAXBHandle<MarkLogicJobInstance> jaxbHandle = xmlDocMgr.read(uri, new JAXBHandle<MarkLogicJobInstance>(jaxbContext()));
			MarkLogicJobInstance mji = jaxbHandle.get();
			return mji.getJobInstance();
		}
		
	
	}

	@Override
	public JobInstance getJobInstance(JobExecution jobExecution) {
		Assert.notNull(jobExecution.getId());
		return getJobInstance(jobExecution.getJobInstance().getId());
	}

	@Override
	public List<JobInstance> getJobInstances(String jobName, int start, int count) {
		QueryManager queryMgr = databaseClient.newQueryManager();    	
    	StringQueryDefinition querydef = queryMgr.newStringDefinition(SEARCH_OPTIONS_NAME);
    	querydef.setCriteria("jobName: " + jobName + " AND sort:date");
    	logger.info(querydef.getCriteria());
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle()); 
    	List<JobInstance> jobInstances = new ArrayList<>();
		MatchDocumentSummary[] summaries = results.getMatchResults();
		MarkLogicJobInstance mji;
		if (start+count > summaries.length) {
			return jobInstances;
		}
		for (int i = start; i < start+count; i++) {
			JAXBHandle<MarkLogicJobInstance> jaxbHandle = new JAXBHandle<>(jaxbContext());
			summaries[i].getFirstSnippet(jaxbHandle);
			mji = jaxbHandle.get();
			jobInstances.add(mji.getJobInstance());
		}
		return jobInstances;
	}

	@Override
	public List<String> getJobNames() {
		List<String> jobNames = new ArrayList<>();
		QueryManager queryMgr = databaseClient.newQueryManager();
		ValuesDefinition valuesDef = queryMgr.newValuesDefinition("jobName", SEARCH_OPTIONS_NAME);
		ValuesHandle results = queryMgr.values(valuesDef, new ValuesHandle());
		for (CountedDistinctValue value : results.getValues()) {
			jobNames.add(value.get("xs:string", String.class));
		}
		return jobNames;
	}

	@Override
	public List<JobInstance> findJobInstancesByName(String jobName, int start, int count) {
		List<JobInstance> jobInstances = new ArrayList<>();
		QueryManager queryMgr = databaseClient.newQueryManager();
		StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef =
				qb.and(
						qb.valueConstraint("jobName", jobName),
						qb.collection(COLLECTION_JOB_INSTANCE)
				);
		queryMgr.setPageLength((long) count);
		SearchHandle results = queryMgr.search(querydef, new SearchHandle(), start);
		MatchDocumentSummary[] summaries = results.getMatchResults();

		for (MatchDocumentSummary summary : summaries ) {
			JAXBHandle<MarkLogicJobInstance> jaxbHandle = new JAXBHandle<>(jaxbContext());
			summary.getFirstSnippet(jaxbHandle);
			MarkLogicJobInstance mji = jaxbHandle.get();
			jobInstances.add(mji.getJobInstance());
		}
		return jobInstances;
	}

	@Override
	public int getJobInstanceCount(String jobName) throws NoSuchJobException {
		StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
		StructuredQueryDefinition querydef =
				qb.and(
						qb.valueConstraint("jobName", jobName),
						qb.collection(COLLECTION_JOB_INSTANCE)
				);
		QueryManager queryMgr = databaseClient.newQueryManager();
		SearchHandle results = queryMgr.search(querydef, new SearchHandle());
		int count = (int) results.getTotalResults();
		if (count == 0) {
			throw new NoSuchJobException(jobName + " not found");
		} else {
			return count;
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
	
	private void validateJobInstanceParameters(String jobName, JobParameters jobParameters) {
		Assert.notNull(jobName, "Job name must not be null.");
		Assert.notNull(jobParameters, "JobParameters must not be null.");
	}



}
