package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.test.AbstractJobRepositoryTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class MarkLogicJobInstanceDaoTests extends AbstractJobRepositoryTest {

    private static final long DATE = 777;

    private String fooJob = "foo";

    private JobParameters fooParams = new JobParametersBuilder().addString("stringKey", "stringValue")
            .addLong("longKey", new Long(50L)).addDouble("doubleKey", new Double(1.0D))
            .addDate("dateKey", new Date(DATE)).toJobParameters();

    private JobInstanceDao jobInstanceDao;

    @Autowired
    private BatchProperties batchProperties;

    @Before
    public void onSetUp() throws Exception {
        jobInstanceDao = new MarkLogicJobInstanceDao(getClient(), batchProperties);
    }

    /*
     * Create and retrieve a job instance.
     */
    @Transactional
    @Test
    public void testCreateAndRetrieve() throws Exception {

        JobInstance fooInstance = jobInstanceDao.createJobInstance(fooJob, fooParams);
        assertNotNull(fooInstance.getId());
        assertEquals(fooJob, fooInstance.getJobName());

        JobInstance retrievedInstance = jobInstanceDao.getJobInstance(fooJob, fooParams);
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

        JobInstance fooInstance = jobInstanceDao.createJobInstance(fooJob, jobParameters);
        assertNotNull(fooInstance.getId());
        assertEquals(fooJob, fooInstance.getJobName());

        JobInstance retrievedInstance = jobInstanceDao.getJobInstance(fooJob, jobParameters);
        assertEquals(fooInstance, retrievedInstance);
        assertEquals(fooJob, retrievedInstance.getJobName());
    }

    /*
     * Create and retrieve a job instance.
     */
    @Transactional
    @Test
    public void testCreateAndGetById() throws Exception {

        JobInstance fooInstance = jobInstanceDao.createJobInstance(fooJob, fooParams);
        assertNotNull(fooInstance.getId());
        assertEquals(fooJob, fooInstance.getJobName());

        JobInstance retrievedInstance = jobInstanceDao.getJobInstance(fooInstance.getId());
        assertEquals(fooInstance, retrievedInstance);
        assertEquals(fooJob, retrievedInstance.getJobName());
    }

    /*
     * Create and retrieve a job instance.
     */
    @Transactional
    @Test
    public void testGetMissingById() throws Exception {

        JobInstance retrievedInstance = jobInstanceDao.getJobInstance(1111111L);
        assertNull(retrievedInstance);

    }

    /*
     * Create and retrieve a job instance.
     */
    @Transactional
    @Test
    public void testGetJobNames() throws Exception {

        testCreateAndRetrieve();
        List<String> jobNames = jobInstanceDao.getJobNames();
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
        jobInstanceDao.createJobInstance("anotherJob", new JobParameters());

        // we need two instances of the same job to check ordering
        jobInstanceDao.createJobInstance(fooJob, new JobParameters());

        List<JobInstance> jobInstances = jobInstanceDao.getJobInstances(fooJob, 0, 2);
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
        jobInstanceDao.createJobInstance("anotherJob", new JobParameters());

        // we need multiple instances of the same job to check ordering
        String multiInstanceJob = "multiInstanceJob";
        String paramKey = "myID";
        int instanceCount = 6;
        for (int i = 1; i <= instanceCount; i++) {
            JobParameters params = new JobParametersBuilder().addLong(paramKey, (long) i).toJobParameters();
            jobInstanceDao.createJobInstance(multiInstanceJob, params);
        }


        int startIndex = 3;
        int queryCount = 2;
        List<JobInstance> jobInstances = jobInstanceDao.getJobInstances(multiInstanceJob, startIndex, queryCount);

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
        jobInstanceDao.createJobInstance("anotherJob", new JobParameters());

        // we need two instances of the same job to check ordering
        jobInstanceDao.createJobInstance(fooJob, new JobParameters());

        List<JobInstance> jobInstances = jobInstanceDao.getJobInstances(fooJob, 4, 2);
        assertEquals(0, jobInstances.size());

    }

    /**
     * Trying to create instance twice for the same job+parameters causes error
     */
    @Transactional
    @Test
    public void testCreateDuplicateInstance() {

        jobInstanceDao.createJobInstance(fooJob, fooParams);

        try {
            jobInstanceDao.createJobInstance(fooJob, fooParams);
            fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Transactional
    @Test
    public void testCreationAddsVersion() {

        JobInstance jobInstance = new JobInstance((long) 1, "testVersionAndId");

        assertNull(jobInstance.getVersion());

        jobInstance = jobInstanceDao.createJobInstance("testVersion", new JobParameters());

        assertNotNull(jobInstance.getVersion());
    }

    public void testGetJobInstanceByExecutionId() {
        // TODO: test this (or maybe the method isn't needed or has wrong signature)
    }

}