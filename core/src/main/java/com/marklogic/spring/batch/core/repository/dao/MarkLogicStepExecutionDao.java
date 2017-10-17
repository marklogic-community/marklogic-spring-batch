package com.marklogic.spring.batch.core.repository.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.marklogic.spring.batch.jdbc.support.incrementer.UriIncrementer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.marklogic.client.DatabaseClient;
import com.marklogic.spring.batch.core.AdaptedStepExecution;

@Component
public class MarkLogicStepExecutionDao extends AbstractMarkLogicBatchMetadataDao implements StepExecutionDao {

	private JobExecutionDao jobExecutionDao;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public MarkLogicStepExecutionDao(DatabaseClient databaseClient, JobExecutionDao dao) {
		this.databaseClient = databaseClient;
		this.jobExecutionDao = dao;
		this.incrementer = new UriIncrementer();
	}

	@Override
	public void saveStepExecution(StepExecution stepExecution) {
		Assert.isTrue(stepExecution.getId() == null);
		Assert.isTrue(stepExecution.getVersion() == null);
		
		Assert.notNull(stepExecution.getJobExecutionId(), "JobExecution must be saved already.");
		JobExecution jobExecution = jobExecutionDao.getJobExecution(stepExecution.getJobExecution().getId());
		Assert.notNull(jobExecution, "JobExecution must be saved already.");
		
		validateStepExecution(stepExecution);
		
		stepExecution.setId(incrementer.nextLongValue());
		stepExecution.incrementVersion();
		
		List<StepExecution> stepExecutions = new ArrayList<>(stepExecution.getJobExecution().getStepExecutions());
		stepExecutions.add(stepExecution);
		jobExecution.addStepExecutions(stepExecutions);
		jobExecutionDao.updateJobExecution(jobExecution);
		logger.info("insert step execution: " + stepExecution.getId() + ",jobExecution:" + jobExecution.getId());
	}

	@Override
	public void saveStepExecutions(Collection<StepExecution> stepExecutions) {
		Assert.notNull(stepExecutions, "Attempt to save a null collection of step executions");

        if (!stepExecutions.isEmpty()) {
        	
        	Long jobExecutionId = stepExecutions.iterator().next().getJobExecutionId();
        	Assert.notNull(jobExecutionId, "JobExecution must be saved already.");
    		JobExecution jobExecution = jobExecutionDao.getJobExecution(jobExecutionId);
    		Assert.notNull(jobExecution, "JobExecution must be saved already.");
        	
        	List<StepExecution> stepExecutionList = new ArrayList<>();
        	
        	for (StepExecution stepExecution : stepExecutions) {
        		Assert.isTrue(stepExecution.getId() == null);
        		Assert.isTrue(stepExecution.getVersion() == null);
        		validateStepExecution(stepExecution);
        		
        		stepExecution.setId(incrementer.nextLongValue());
        		stepExecution.incrementVersion();
        		stepExecutionList.add(stepExecution);
        	}
        	
        	jobExecution.addStepExecutions(stepExecutionList);
        	jobExecutionDao.updateJobExecution(jobExecution);
        }	
	}

	@Override
	public void updateStepExecution(StepExecution stepExecution) {
		validateStepExecution(stepExecution);
		Assert.notNull(stepExecution.getId(), "StepExecution Id cannot be null. StepExecution must saved"
				+ " before it can be updated.");
		
		
		Assert.notNull(stepExecution.getJobExecutionId(), "JobExecution must be saved already.");
		JobExecution jobExecution = jobExecutionDao.getJobExecution(stepExecution.getJobExecutionId());
		Assert.notNull(jobExecution, "JobExecution must be saved already.");
		
		validateStepExecution(stepExecution);

		Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();
		synchronized (stepExecution) {
			
			
			for (StepExecution se : stepExecutions) {
				if (se.getId().equals(stepExecution.getId())) {
					stepExecution.incrementVersion();
					copy(stepExecution, se);
				}
			}
			List<StepExecution> steps = new ArrayList<>(stepExecutions);
			stepExecution.getJobExecution().addStepExecutions(steps);
			jobExecutionDao.updateJobExecution(jobExecution);
			logger.info("update step execution: " + stepExecution.getId() + ",jobExecution:" + jobExecution.getId());
		}
	}

	@Override
	public StepExecution getStepExecution(JobExecution jobExecution, Long stepExecutionId) {
		JobExecution je = jobExecutionDao.getJobExecution(jobExecution.getId());
		if (je == null) {
			return null;
		}
		List<StepExecution> executions = new ArrayList<>(je.getStepExecutions());
		
		if (executions.isEmpty()) {
			return null;
		}
		
		StepExecution execution = null;
		for (StepExecution se : executions) {
			if (se.getId().equals(stepExecutionId)) {
				execution = se;
			}
		}

		if (execution == null) {
			return null;
		} else {
			return execution;
		}
	}

	@Override
	public void addStepExecutions(JobExecution jobExecution) {
		Collection<StepExecution> stepExecutions = jobExecutionDao.getJobExecution(jobExecution.getId()).getStepExecutions();
		List<StepExecution> stepExecutionList = new ArrayList<>(stepExecutions);
		jobExecution.addStepExecutions(stepExecutionList);

	}
	
	private void validateStepExecution(StepExecution stepExecution) {
		Assert.notNull(stepExecution);
		Assert.notNull(stepExecution.getStepName(), "StepExecution step name cannot be null.");
		Assert.notNull(stepExecution.getStartTime(), "StepExecution start time cannot be null.");
		Assert.notNull(stepExecution.getStatus(), "StepExecution status cannot be null.");
	}

	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext;
		try {
            jaxbContext = JAXBContext.newInstance(AdaptedStepExecution.class);
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
/*
	private static StepExecution copy(StepExecution original) {
		return (StepExecution) SerializationUtils.deserialize(SerializationUtils.serialize(original));
	}
*/
	private static void copy(final StepExecution sourceExecution, final StepExecution targetExecution) {
		// Cheaper than full serialization is a reflective field copy, which is
		// fine for volatile storage
		ReflectionUtils.doWithFields(StepExecution.class, new ReflectionUtils.FieldCallback() {
			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				field.setAccessible(true);
				field.set(targetExecution, field.get(sourceExecution));
			}
		}, ReflectionUtils.COPYABLE_FIELDS);
	}
}
