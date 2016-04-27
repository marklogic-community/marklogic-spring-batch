package com.marklogic.spring.batch.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.batch.operations.JobRestartException;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.util.Assert;

public class JobRepositoryTestUtils implements InitializingBean {
	
	private JobRepository jobRepository;
	
	private JobParametersIncrementer jobParametersIncrementer = new JobParametersIncrementer() {

		Long count = 0L;

		@Override
		public JobParameters getNext(JobParameters parameters) {
			return new JobParameters(Collections.singletonMap("count", new JobParameter(count++)));
		}

	};
	
	/**
	 * Default constructor.
	 */
	public JobRepositoryTestUtils() {
	}
	
	/**
	 * Create a {@link JobRepositoryTestUtils} with all its mandatory
	 * properties.
	 *
	 * @param jobRepository a {@link JobRepository} backed by a database
	 */
	public JobRepositoryTestUtils(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}
	
	/**
	 * @see InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(jobRepository, "JobRepository must be set");
	}
	
	/**
	 * @param jobRepository the jobRepository to set
	 */
	public void setJobRepository(JobRepository jobRepository) {
		this.jobRepository = jobRepository;
	}
	
	/**
	 * Use the {@link JobRepository} to create some {@link JobExecution}
	 * instances each with the given job name and each having step executions
	 * with the given step names.
	 *
	 * @param jobName the name of the job
	 * @param stepNames the names of the step executions
	 * @param count the required number of instances of {@link JobExecution} to
	 * create
	 * @return a collection of {@link JobExecution}
	 * @throws org.springframework.batch.core.repository.JobRestartException 
	 */
	public List<JobExecution> createJobExecutions(String jobName, String[] stepNames, int count)
			throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, org.springframework.batch.core.repository.JobRestartException {
		List<JobExecution> list = new ArrayList<JobExecution>();
		JobParameters jobParameters = new JobParameters();
		for (int i = 0; i < count; i++) {
			JobExecution jobExecution = jobRepository.createJobExecution(jobName, jobParametersIncrementer
					.getNext(jobParameters));
			list.add(jobExecution);
			for (String stepName : stepNames) {
				jobRepository.add(jobExecution.createStepExecution(stepName));
			}
		}
		return list;
	}

	/**
	 * Use the {@link JobRepository} to create some {@link JobExecution}
	 * instances each with a single step execution.
	 *
	 * @param count the required number of instances of {@link JobExecution} to
	 * create
	 * @return a collection of {@link JobExecution}
	 * @throws org.springframework.batch.core.repository.JobRestartException 
	 */
	public List<JobExecution> createJobExecutions(int count) throws JobExecutionAlreadyRunningException,
	JobRestartException, JobInstanceAlreadyCompleteException, org.springframework.batch.core.repository.JobRestartException {
		return createJobExecutions("job", new String[] { "step" }, count);
	}
	
	/**
	 * Remove the {@link JobExecution} instances, and all associated
	 * {@link JobInstance} and {@link StepExecution} instances from the standard
	 * RDBMS locations used by Spring Batch.
	 *
	 * @param list a list of {@link JobExecution}
	 * @throws DataAccessException if there is a problem
	 */
	public void removeJobExecutions(Collection<JobExecution> list) throws DataAccessException {
		for (JobExecution jobExecution : list) {
			Long id = jobExecution.getId();
			
		}
	}

}