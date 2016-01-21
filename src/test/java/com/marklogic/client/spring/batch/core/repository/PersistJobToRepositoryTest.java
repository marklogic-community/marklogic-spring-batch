package com.marklogic.client.spring.batch.core.repository;

import org.junit.Test;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.marklogic.client.spring.batch.AbstractSpringBatchTest;

public class PersistJobToRepositoryTest extends AbstractSpringBatchTest {
	
	@Autowired
	JobRepository jobRepository;
	
	@Test
	public void persistJobToRepositoryTest() throws Exception {
		jobRepositoryTestUtils.setJobRepository(jobRepository);
		String[] stepNames = new String[1];
		stepNames[0] = "abc";
		
		jobRepositoryTestUtils.createJobExecutions("hello", stepNames, 1);
	}

}
