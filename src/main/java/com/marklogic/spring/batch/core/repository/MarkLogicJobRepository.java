package com.marklogic.spring.batch.core.repository;

import java.util.Collection;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

public class MarkLogicJobRepository implements JobRepository, InitializingBean {
	
	@Autowired
	ApplicationContext ctx;

    private DocumentBuilder documentBuilder;
    private JAXBContext jaxbContext;
    private DocumentMetadataHandle jobExecutionMetadata;
    private DatabaseClient client;
    
    private static Logger logger = Logger.getLogger("com.marklogic.spring.batch.core.repository.MarkLogicJobRepository");
    
	private final String SEARCH_OPTIONS_NAME = "spring-batch";

    public MarkLogicJobRepository(DatabaseClient client) {
        this.client = client;
        initializeDocumentBuilder();
        try {
            jaxbContext = JAXBContext.newInstance(AdaptedJobExecution.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        jobExecutionMetadata = new DocumentMetadataHandle();
        jobExecutionMetadata.getCollections().add(MarkLogicSpringBatch.COLLECTION_JOB_EXECUTION);

    }

    protected void initializeDocumentBuilder() {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        try {
            this.documentBuilder = domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isJobInstanceExists(String jobName, JobParameters jobParameters) {
    	QueryManager queryMgr = client.newQueryManager();
    	StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
    	String query = 
				"<query xmlns=\"http://marklogic.com/appservices/search\">" + 
						"<container-constraint-query>" + 
							"<constraint-name>jobInstance</constraint-name>" + 
							"<value-constraint-query>" +
								"<constraint-name>jobName</constraint-name>" +
								"<text>" + jobName + "</text>" +
							"</value-constraint-query>" +
						"</container-constraint-query>" +
				"</query>";
        return false;
    }

    @Override
    public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
    	return null;
    }

    @Override
    public JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters,
            String jobConfigurationLocation) {
        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters, jobConfigurationLocation);
        Document doc = documentBuilder.newDocument();
        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(jobExecution, doc);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
        DOMHandle handle = new DOMHandle();
        handle.set(doc);
        xmlDocMgr.write("/tes123t.xml", handle);

        return jobExecution;
    }

    @Override
    public JobExecution createJobExecution(String jobName, JobParameters jobParameters)
            throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        // Check to see if JobInstance exists
    	if (isJobInstanceExists(jobName, jobParameters)) {
    		
    	} else {
    		
    	}
    	
        JobInstance jobInstance = new JobInstance(getRandomNumber(), jobName);
        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);
        jobExecution.setId(getRandomNumber());
        AdaptedJobExecution batchJob = new AdaptedJobExecution(jobExecution);
        
        Document doc = documentBuilder.newDocument();
        
        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(batchJob, doc);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
        DOMHandle handle = new DOMHandle();
        handle.set(doc);
        xmlDocMgr.write(MarkLogicSpringBatch.SPRING_BATCH_DIR + "/job-execution/" + jobExecution.getId().toString(), jobExecutionMetadata,
                handle);

        return jobExecution;
    }

    @Override
    public void update(JobExecution jobExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public void add(StepExecution stepExecution) {
    	validateStepExecution(stepExecution);
    	stepExecution.setLastUpdated(new Date(System.currentTimeMillis()));		
    }

    @Override
    public void addAll(Collection<StepExecution> stepExecutions) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(StepExecution stepExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateExecutionContext(StepExecution stepExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateExecutionContext(JobExecution jobExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStepExecutionCount(JobInstance jobInstance, String stepName) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public JobExecution getLastJobExecution(String jobName, JobParameters jobParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    private long getRandomNumber() {
        long LOWER_RANGE = 0; // assign lower range value
        long UPPER_RANGE = 9999999; // assign upper range value
        Random random = new Random();
        return LOWER_RANGE + (long) (random.nextDouble() * (UPPER_RANGE - LOWER_RANGE));
    }
    
    private void validateStepExecution(StepExecution stepExecution) {
		Assert.notNull(stepExecution, "StepExecution cannot be null.");
		Assert.notNull(stepExecution.getStepName(), "StepExecution's step name cannot be null.");
		Assert.notNull(stepExecution.getJobExecutionId(), "StepExecution must belong to persisted JobExecution");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		QueryOptionsManager queryOptionsMgr = client.newServerConfigManager().newQueryOptionsManager();
		Resource options = ctx.getResource("classpath:/options/spring-batch-options.xml");
		InputStreamHandle handle = new InputStreamHandle(options.getInputStream());
		queryOptionsMgr.writeOptions(SEARCH_OPTIONS_NAME, handle);
		logger.info(SEARCH_OPTIONS_NAME + " options loaded");
		QueryOptionsListHandle qolHandle = queryOptionsMgr.optionsList(new QueryOptionsListHandle());
		Set<String> results = qolHandle.getValuesMap().keySet();
		assert(results.contains(SEARCH_OPTIONS_NAME) == true);
	}

}
