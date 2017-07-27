package com.marklogic.spring.batch.core.repository.dao;

import java.util.Date;
import java.util.List;

import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.transaction.annotation.Transactional;

public class MarkLogicJobInstanceDaoTests extends AbstractJobRepositoryTest {

	private static final long DATE = 777;
	
	private String fooJob = "foo";

	private JobParameters fooParams = new JobParametersBuilder().addString("stringKey", "stringValue")
			.addLong("longKey", Long.MAX_VALUE).addDouble("doubleKey", Double.MAX_VALUE)
			.addDate("dateKey", new Date(DATE)).toJobParameters();

	@Before
	public void onSetUp() throws Exception {
		initializeJobRepository();
	}

	/*
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testCreateAndRetrieve() throws Exception {

		JobInstance fooInstance = getJobInstanceDao().createJobInstance(fooJob, fooParams);
		assertNotNull(fooInstance.getId());
		assertEquals(fooJob, fooInstance.getJobName());

		JobInstance retrievedInstance = getJobInstanceDao().getJobInstance(fooJob, fooParams);
		assertEquals(fooInstance, retrievedInstance);
		assertEquals(fooJob, retrievedInstance.getJobName());
	}

	/*
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testCreateAndRetrieveWithNullParameter() throws Exception {

		JobParameters jobParameters = new JobParametersBuilder().addString("foo", null).toJobParameters();

		JobInstance fooInstance = getJobInstanceDao().createJobInstance(fooJob, jobParameters);
		assertNotNull(fooInstance.getId());
		assertEquals(fooJob, fooInstance.getJobName());

		JobInstance retrievedInstance = getJobInstanceDao().getJobInstance(fooJob, jobParameters);
		assertEquals(fooInstance, retrievedInstance);
		assertEquals(fooJob, retrievedInstance.getJobName());
	}

	/*
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testCreateAndGetById() throws Exception {

		JobInstance fooInstance = getJobInstanceDao().createJobInstance(fooJob, fooParams);
		assertNotNull(fooInstance.getId());
		assertEquals(fooJob, fooInstance.getJobName());

		JobInstance retrievedInstance = getJobInstanceDao().getJobInstance(fooInstance.getId());
		assertEquals(fooInstance, retrievedInstance);
		assertEquals(fooJob, retrievedInstance.getJobName());
	}

	/*
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testGetMissingById() throws Exception {

		JobInstance retrievedInstance = getJobInstanceDao().getJobInstance(1111111L);
		assertNull(retrievedInstance);

	}

	/*
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testGetJobNames() throws Exception {

		testCreateAndRetrieve();
		List<String> jobNames = getJobInstanceDao().getJobNames();
		assertFalse(jobNames.isEmpty());
		assertTrue(jobNames.contains(fooJob));

	}

	/**
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testGetLastInstances() throws Exception {

		testCreateAndRetrieve();

		// unrelated job instance that should be ignored by the query
		getJobInstanceDao().createJobInstance("anotherJob", new JobParameters());

		// we need two instances of the same job to check ordering
		getJobInstanceDao().createJobInstance(fooJob, new JobParameters());

		List<JobInstance> jobInstances = getJobInstanceDao().getJobInstances(fooJob, 0, 2);
		assertEquals(2, jobInstances.size());
		assertEquals(fooJob, jobInstances.get(0).getJobName());
		assertEquals(fooJob, jobInstances.get(1).getJobName());
		assertEquals(Integer.valueOf(0), jobInstances.get(0).getVersion());
		assertEquals(Integer.valueOf(0), jobInstances.get(1).getVersion());

		//assertTrue("Last instance should be first on the list", jobInstances.get(0).getCreateDateTime() > jobInstances.get(1)
			//	.getId());

	}

	/**
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testGetLastInstancesPaged() throws Exception {

		testCreateAndRetrieve();

		// unrelated job instance that should be ignored by the query
		getJobInstanceDao().createJobInstance("anotherJob", new JobParameters());

		// we need multiple instances of the same job to check ordering
		String multiInstanceJob = "multiInstanceJob";
		String paramKey = "myID";
		int instanceCount = 6;
		for (int i = 1; i <= instanceCount; i++) {
			JobParameters params = new JobParametersBuilder().addLong(paramKey, (long) i).toJobParameters();
			getJobInstanceDao().createJobInstance(multiInstanceJob, params);
		}


		int startIndex = 3;
		int queryCount = 2;
		List<JobInstance> jobInstances = getJobInstanceDao().getJobInstances(multiInstanceJob, startIndex, queryCount);

		assertEquals(queryCount, jobInstances.size());

		for (int i = 0; i < queryCount; i++) {
			JobInstance returnedInstance = jobInstances.get(i);
			assertEquals(multiInstanceJob, returnedInstance.getJobName());
			assertEquals(Integer.valueOf(0), returnedInstance.getVersion());

			//checks the correct instances are returned and the order is descending
			//			assertEquals(instanceCount - startIndex - i , returnedInstance.getJobParameters().getLong(paramKey));
		}

	}

	/**
	 * Create and retrieve a job instance.
	 */
	@Transactional
	@Test
	public void testGetLastInstancesPastEnd() throws Exception {

		testCreateAndRetrieve();

		// unrelated job instance that should be ignored by the query
		getJobInstanceDao().createJobInstance("anotherJob", new JobParameters());

		// we need two instances of the same job to check ordering
		getJobInstanceDao().createJobInstance(fooJob, new JobParameters());

		List<JobInstance> jobInstances = getJobInstanceDao().getJobInstances(fooJob, 4, 2);
		assertEquals(0, jobInstances.size());

	}

	/**
	 * Trying to create instance twice for the same job+parameters causes error
	 */
	@Transactional
	@Test
	public void testCreateDuplicateInstance() {
		
		getJobInstanceDao().createJobInstance(fooJob, fooParams);

		try {
			getJobInstanceDao().createJobInstance(fooJob, fooParams);
			fail();
		}
		catch (IllegalStateException e) {
			// expected
		}
	}

	@Transactional
	@Test
	public void testCreationAddsVersion() {

		JobInstance jobInstance = new JobInstance((long) 1, "testVersionAndId");

		assertNull(jobInstance.getVersion());

		jobInstance = getJobInstanceDao().createJobInstance("testVersion", new JobParameters());

		assertNotNull(jobInstance.getVersion());
	}

	public void testGetJobInstanceByExecutionId() {
		// TODO: test this (or maybe the method isn't needed or has wrong signature)
	}

}