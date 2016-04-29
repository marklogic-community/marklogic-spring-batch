package com.marklogic.spring.batch.core.repository.dao;

import java.security.SecureRandom;
import java.util.List;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class MarkLogicJobInstanceDao extends AbstractMarkLogicBatchMetadataDao implements JobInstanceDao, InitializingBean {
	
	@Autowired
	SecureRandom random;

	@Override
	public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
		Assert.notNull(jobName, "Job name must not be null.");
		Assert.notNull(jobParameters, "JobParameters must not be null.");

		Assert.state(getJobInstance(jobName, jobParameters) == null,
				"JobInstance must not already exist");
		
		JobInstance jobInstance = new JobInstance(generateId(), jobName);
    	return jobInstance;
	}

	@Override
	public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobInstance getJobInstance(Long instanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobInstance getJobInstance(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobInstance> getJobInstances(String jobName, int start, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getJobNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobInstance> findJobInstancesByName(String jobName, int start, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getJobInstanceCount(String jobName) throws NoSuchJobException {
		// TODO Auto-generated method stub
		return 0;
	}



}
