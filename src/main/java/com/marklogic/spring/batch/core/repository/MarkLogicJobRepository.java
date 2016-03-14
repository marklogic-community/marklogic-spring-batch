package com.marklogic.spring.batch.core.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
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
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.QueryOptionsListHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

public class MarkLogicJobRepository implements JobRepository, InitializingBean {
	
	@Autowired
	private ApplicationContext ctx;

	@Autowired
	private JobExplorer jobExplorer;
	
    private DocumentBuilder documentBuilder;
    private DocumentMetadataHandle jobExecutionMetadata;
    private DatabaseClient client;
    private XMLDocumentManager xmlDocMgr;
    private QueryManager queryMgr;
    
    private static Logger logger = Logger.getLogger("com.marklogic.spring.batch.core.repository.MarkLogicJobRepository");
    
	public final static String SEARCH_OPTIONS_NAME = "spring-batch";
	
	public MarkLogicJobRepository() {
		
	}

    public MarkLogicJobRepository(DatabaseClient client) {
        this.client = client;
        initializeDocumentBuilder();
        jobExecutionMetadata = new DocumentMetadataHandle();
        jobExecutionMetadata.getCollections().add(MarkLogicSpringBatch.COLLECTION_JOB_EXECUTION);
        xmlDocMgr = client.newXMLDocumentManager();
        queryMgr = client.newQueryManager();
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
    	StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
    	List<StructuredQueryDefinition> paramValues = new ArrayList<StructuredQueryDefinition>();
    	for (String paramName : jobParameters.getParameters().keySet()) {
    		JobParameter param = jobParameters.getParameters().get(paramName);
    		if (param.isIdentifying()) {
    			paramValues.add(qb.valueConstraint("jobParameter", param.getValue().toString()));
    		}
    	}
    	paramValues.add(qb.valueConstraint("jobName", jobName));
    	StructuredQueryDefinition querydef = qb.and(paramValues.toArray(new StructuredQueryDefinition[paramValues.size()]));
    	logger.finer(querydef.serialize());
    	SearchHandle results = queryMgr.search(querydef, new SearchHandle());
		return (results.getTotalResults() > 0);
    }

    @Override
    public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
    	JobExecution jobExecution = null;
		try {
			jobExecution = createJobExecution(jobName, jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException e) {
			e.printStackTrace();
		}
    	return jobExecution.getJobInstance();
    }

    @Override
    public JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters,
            String jobConfigurationLocation) {
        JobExecution jobExecution = new JobExecution(jobInstance, getRandomNumber(), jobParameters, jobConfigurationLocation);
        update(jobExecution);
        return jobExecution;
    }
   
    @Override
    public JobExecution createJobExecution(String jobName, JobParameters jobParameters)
            throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {	
    	JobInstance jobInstance = null;
    	JobExecution jobExecution = null;
    	if (isJobInstanceExists(jobName, jobParameters)) {
    		Set<JobExecution> jobExecutions = jobExplorer.findRunningJobExecutions(jobName);
    		if (jobExecutions.size() > 0) {
    			throw new JobExecutionAlreadyRunningException(jobName);
    		}   
    		boolean isJobFailed = false;
    		for (JobExecution je : getJobExecutions(jobName, jobParameters)) {
    			if (je.getStatus().equals(BatchStatus.COMPLETED)) {
    				throw new JobInstanceAlreadyCompleteException(jobName);
    			} else if (je.getStatus().isUnsuccessful()) {
    				isJobFailed = true;
    				jobInstance = je.getJobInstance();
    			}
    		}
    		if (isJobFailed) {
    			jobExecution = new JobExecution(jobInstance, getRandomNumber(), jobParameters, null);
           		update(jobExecution);
    		}
    	} else {
    		jobInstance = new JobInstance(getRandomNumber(), jobName);
       		jobExecution = new JobExecution(jobInstance, getRandomNumber(), jobParameters, null);
       		update(jobExecution);
    	}      
        return jobExecution;
    }

    private List<JobExecution> getJobExecutions(String jobName, JobParameters jobParameters) {   	
    	StructuredQueryBuilder qb = new StructuredQueryBuilder(SEARCH_OPTIONS_NAME);
    	List<StructuredQueryDefinition> paramValues = new ArrayList<StructuredQueryDefinition>();
    	for (String paramName : jobParameters.getParameters().keySet()) {
    		JobParameter param = jobParameters.getParameters().get(paramName);
    		if (param.isIdentifying()) {
    			paramValues.add(qb.valueConstraint("jobParameter", param.getValue().toString()));
    		}
    	}
    	paramValues.add(qb.valueConstraint("jobName", jobName));
    	StructuredQueryDefinition querydef = qb.and(paramValues.toArray(new StructuredQueryDefinition[paramValues.size()]));
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
				logger.severe(ex.getMessage());
			}
		}
		return jobExecutions;
    }
    
    @Override
    public void update(JobExecution jobExecution) {
        try {
        	Document doc = documentBuilder.newDocument();
            Marshaller marshaller = jaxbContext().createMarshaller();
            JobExecutionAdapter adapter = new JobExecutionAdapter();
            AdaptedJobExecution aje = adapter.marshal(jobExecution);
            marshaller.marshal(aje, doc);
            DOMHandle handle = new DOMHandle();
            handle.set(doc);
            xmlDocMgr.write(MarkLogicSpringBatch.SPRING_BATCH_DIR + jobExecution.getId().toString() + ".xml", jobExecutionMetadata, handle);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (Exception e) {
        	e.printStackTrace();
        }
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
        JobExecution jobExecution = null;
        return jobExecution;
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
	
	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext = null;
		try {
            jaxbContext = JAXBContext.newInstance(AdaptedJobExecution.class, AdaptedJobInstance.class, AdaptedJobParameters.class, AdaptedStepExecution.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
		return jaxbContext;
	}

}
