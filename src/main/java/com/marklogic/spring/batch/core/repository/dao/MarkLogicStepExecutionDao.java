package com.marklogic.spring.batch.core.repository.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.util.Assert;

import com.marklogic.client.DatabaseClient;
import com.marklogic.spring.batch.core.AdaptedStepExecution;

public class MarkLogicStepExecutionDao extends AbstractMarkLogicBatchMetadataDao implements StepExecutionDao {
	
	private static final Log logger = LogFactory.getLog(MarkLogicJobInstanceDao.class);
	
	private JobExecutionDao jobExecutionDao;
	
	public MarkLogicStepExecutionDao(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public void saveStepExecution(StepExecution stepExecution) {
		Assert.isTrue(stepExecution.getId() == null);
		Assert.isTrue(stepExecution.getVersion() == null);
		
		Assert.notNull(stepExecution.getJobExecutionId(), "JobExecution must be saved already.");
		JobExecution jobExecution = jobExecutionDao.getJobExecution(stepExecution.getJobExecutionId());
		Assert.notNull(jobExecution, "JobExecution must be saved already.");
		
		validateStepExecution(stepExecution);
		
		stepExecution.setId(incrementer.nextLongValue());
		stepExecution.incrementVersion();
		
		List<StepExecution> stepExecutions = new ArrayList<StepExecution>();
		stepExecutions.add(stepExecution);
		jobExecution.addStepExecutions(stepExecutions);
		logger.info("insert step execution: " + stepExecution.getId() + ",jobExecution:" + jobExecution.getId());
    	return;

	}

	@Override
	public void saveStepExecutions(Collection<StepExecution> stepExecutions) {
		Assert.notNull(stepExecutions, "Attempt to save a null collection of step executions");

        if (!stepExecutions.isEmpty()) {
        	Long jobExecutionId = stepExecutions.iterator().next().getJobExecutionId();
        	JobExecution jobExecution = jobExecutionDao.getJobExecution(jobExecutionId);
        	List<StepExecution> stepExecutionList = new ArrayList<StepExecution>(stepExecutions);
        	jobExecution.addStepExecutions(stepExecutionList);
        	jobExecutionDao.updateJobExecution(jobExecution);
        }	
	}

	@Override
	public void updateStepExecution(StepExecution stepExecution) {
		// TODO Auto-generated method stub

	}

	@Override
	public StepExecution getStepExecution(JobExecution jobExecution, Long stepExecutionId) {
		
		Collection<StepExecution> stepExecutions = jobExecutionDao.getJobExecution(jobExecution.getId()).getStepExecutions();
		for ( StepExecution stepExecution : stepExecutions ) {
			if (stepExecution.getId() == stepExecutionId) {
				return stepExecution;
			}
		}
		return null;
	}

	@Override
	public void addStepExecutions(JobExecution jobExecution) {
		// TODO Auto-generated method stub

	}
	
	private void validateStepExecution(StepExecution stepExecution) {
		Assert.notNull(stepExecution);
		Assert.notNull(stepExecution.getStepName(), "StepExecution step name cannot be null.");
		Assert.notNull(stepExecution.getStartTime(), "StepExecution start time cannot be null.");
		Assert.notNull(stepExecution.getStatus(), "StepExecution status cannot be null.");
	}

	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext = null;
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
}
