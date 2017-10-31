package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.spring.batch.bind.StepExecutionAdapter;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MarkLogicStepExecutionDao implements StepExecutionDao {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseClient databaseClient;
    private BatchProperties properties;
    private JobExecutionDao jobExecutionDao;
    private StepExecutionAdapter adapter;

    @Autowired
    public MarkLogicStepExecutionDao(DatabaseClient databaseClient, JobExecutionDao dao, BatchProperties batchProperties) {
        this.databaseClient = databaseClient;
        this.properties = batchProperties;
        this.jobExecutionDao = dao;
        adapter = new StepExecutionAdapter();
    }

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

    @Override
    public void saveStepExecution(StepExecution stepExecution) {

        Assert.isTrue(stepExecution.getId() == null);
        Assert.isTrue(stepExecution.getVersion() == null);

        Assert.notNull(stepExecution.getJobExecutionId(), "JobExecution must be saved already.");
        JobExecution jobExecution = jobExecutionDao.getJobExecution(stepExecution.getJobExecution().getId());
        Assert.notNull(jobExecution, "JobExecution must be saved already.");

        validateStepExecution(stepExecution);

        stepExecution.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        stepExecution.incrementVersion();

        AdaptedStepExecution adaptedStepExecution = null;
        try {
            adaptedStepExecution = adapter.marshal(stepExecution);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections(properties.getCollection(), properties.getStepExecutionCollection());

        JAXBHandle<AdaptedStepExecution> jaxbHandle = new JAXBHandle<AdaptedStepExecution>(jaxbContext());
        jaxbHandle.set(adaptedStepExecution);

        docMgr.write(getUri(stepExecution), metadata, jaxbHandle);
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

                //stepExecution.setId(incrementer.nextLongValue());
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
        stepExecution.incrementVersion();

        synchronized (stepExecution) {
            XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
            String uri = getUri(stepExecution);
            DocumentMetadataHandle metadata = new DocumentMetadataHandle();
            metadata.withCollections(properties.getCollection(), properties.getStepExecutionCollection());
            AdaptedStepExecution ase = null;
            try {
                ase = adapter.marshal(stepExecution);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
            JAXBHandle<AdaptedStepExecution> jaxbHandle = new JAXBHandle<AdaptedStepExecution>(jaxbContext());
            jaxbHandle.set(ase);
            docMgr.write(uri, metadata, jaxbHandle);
        }



    }

    @Override
    public StepExecution getStepExecution(JobExecution jobExecution, Long stepExecutionId) {
        XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
        String uri = properties.getJobRepositoryDirectory() + "/" +
                jobExecution.getJobInstance().getId() + "/" +
                jobExecution.getId() + "/" +
                stepExecutionId + ".xml";
        JAXBHandle<AdaptedStepExecution> jaxbHandle = new JAXBHandle<AdaptedStepExecution>(jaxbContext());
        DocumentPage page = docMgr.read(uri);
        AdaptedStepExecution ase = page.next().getContent(jaxbHandle).get();
        StepExecution stepExecution = null;
        try {
            stepExecution = adapter.unmarshal(ase);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException(ex);
        }
        return stepExecution;
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

    private String getUri(StepExecution stepExecution) {
        return properties.getJobRepositoryDirectory() + "/" +
                stepExecution.getJobExecution().getJobInstance().getId() + "/" +
                stepExecution.getJobExecution().getId() + "/" +
                stepExecution.getId() + ".xml";
    }

    public JobExecutionDao getJobExecutionDao() {
        return jobExecutionDao;
    }

    public void setJobExecutionDao(JobExecutionDao jobExecutionDao) {
        this.jobExecutionDao = jobExecutionDao;
    }
}
