package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.bind.StepExecutionAdapter;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MarkLogicStepExecutionDao implements StepExecutionDao {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseClient databaseClient;
    private BatchProperties properties;
    private StepExecutionAdapter adapter;

    public MarkLogicStepExecutionDao(DatabaseClient databaseClient, BatchProperties batchProperties) {
        this.databaseClient = databaseClient;
        this.properties = batchProperties;
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

        validateStepExecution(stepExecution);
        stepExecution.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));

        AdaptedStepExecution adaptedStepExecution = null;
        try {
            adaptedStepExecution = adapter.marshal(stepExecution);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections(properties.getCollection(), properties.getStepExecutionCollection());

        JAXBHandle<AdaptedStepExecution> jaxbHandle = new JAXBHandle<AdaptedStepExecution>(jaxbContext());
        jaxbHandle.set(adaptedStepExecution);

        XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
        String uri = getUri(stepExecution);
        docMgr.write(uri, metadata, jaxbHandle);

        DocumentDescriptor desc = docMgr.exists(uri);
        stepExecution.setVersion((int) desc.getVersion());
    }

    @Override
    public void saveStepExecutions(Collection<StepExecution> stepExecutions) {
        Assert.notNull(stepExecutions, "Attempt to save a null collection of step executions");

        if (!stepExecutions.isEmpty()) {

            Long jobExecutionId = stepExecutions.iterator().next().getJobExecutionId();
            Assert.notNull(jobExecutionId, "JobExecution must be saved already.");
            //JobExecution jobExecution = jobExecutionDao.getJobExecution(jobExecutionId);
            //Assert.notNull(jobExecution, "JobExecution must be saved already.");

            for (StepExecution stepExecution : stepExecutions) {
                Assert.isTrue(stepExecution.getId() == null);
                Assert.isTrue(stepExecution.getVersion() == null);
                validateStepExecution(stepExecution);
                saveStepExecution(stepExecution);
            }
        }
    }

    @Override
    public void updateStepExecution(StepExecution stepExecution) {
        validateStepExecution(stepExecution);
        Assert.notNull(stepExecution.getId(), "StepExecution Id cannot be null. StepExecution must saved"
                + " before it can be updated.");


        Assert.notNull(stepExecution.getJobExecutionId(), "JobExecution must be saved already.");

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
            DocumentDescriptor desc = docMgr.exists(uri);
            if (desc == null) {
                logger.error(uri + " does not exist and is attempting an update");
                throw new RuntimeException(uri + " does not exist and is attempting an update");
            } else if (stepExecution.getVersion() != (int) desc.getVersion()) {
                throw new OptimisticLockingFailureException(uri);
            }
            docMgr.write(desc, metadata, jaxbHandle);
            desc = docMgr.exists(uri);
            stepExecution.setVersion((int) desc.getVersion());
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
        if (page.getTotalSize() == 0) {
            return null;
        } else {
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
    }

    @Override
    public void addStepExecutions(JobExecution jobExecution) {
        List<StepExecution> stepExecutionList = new ArrayList<StepExecution>();
        StructuredQueryBuilder qb = new StructuredQueryBuilder(properties.getSearchOptions());
        String directoryUri = properties.getJobRepositoryDirectory() + "/" +
                jobExecution.getJobInstance().getId() + "/" +
                jobExecution.getId() + "/";
        StructuredQueryDefinition querydef = qb.and(
                qb.collection(properties.getStepExecutionCollection()),
                qb.directory(true, directoryUri)
        );
        QueryManager queryMgr = databaseClient.newQueryManager();
        SearchHandle results = queryMgr.search(querydef, new SearchHandle());
        if (results.getTotalResults() > 0L) {
            MatchDocumentSummary[] summaries = results.getMatchResults();
            for (MatchDocumentSummary summary : summaries) {
                JAXBHandle<AdaptedStepExecution> handle = new JAXBHandle<AdaptedStepExecution>(jaxbContext());
                AdaptedStepExecution ase = summaries[0].getFirstSnippet(handle).get();
                StepExecution stepExecution = null;
                try {
                    stepExecution = adapter.unmarshal(ase);
                } catch (Exception ex) {
                    logger.error(ex.getMessage());
                    throw new RuntimeException(ex);
                }
                stepExecutionList.add(stepExecution);
            }
        }
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

}
