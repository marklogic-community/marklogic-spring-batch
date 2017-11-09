package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentManager;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryBuilder.Operator;
import com.marklogic.client.query.StructuredQueryDefinition;
import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.NoSuchObjectException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class MarkLogicJobExecutionDao implements JobExecutionDao {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private DatabaseClient databaseClient;
    private BatchProperties properties;
    private JobExecutionAdapter adapter;

    public MarkLogicJobExecutionDao(DatabaseClient databaseClient, BatchProperties batchProperties) {
        this.databaseClient = databaseClient;
        this.properties = batchProperties;
        this.adapter = new JobExecutionAdapter();
    }

    @Override
    public void saveJobExecution(JobExecution jobExecution) {
        validateJobExecution(jobExecution);

        if (jobExecution.getId() == null) {
            jobExecution.setId(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        }
        AdaptedJobExecution aJobInstance;
        try {
            aJobInstance = adapter.marshal(jobExecution);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new RuntimeException(ex);
        }

        XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
        String uri = getUri(jobExecution);

        //Set document metadata
        DocumentMetadataHandle jobInstanceMetadata = new DocumentMetadataHandle();
        jobInstanceMetadata.withCollections(properties.getCollection(), properties.getJobExecutionCollection());
        JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<AdaptedJobExecution>(jaxbContext());
        handle.set(aJobInstance);

        xmlDocMgr.write(uri, jobInstanceMetadata, handle);
        DocumentDescriptor desc = xmlDocMgr.exists(uri);
        jobExecution.setVersion((int) desc.getVersion());
    }

    /**
     * Validate JobExecution. At a minimum, JobId, StartTime, EndTime, and
     * Status cannot be null.
     *
     * @param jobExecution
     * @throws IllegalArgumentException
     */
    private void validateJobExecution(JobExecution jobExecution) {
        Assert.notNull(jobExecution.getJobId(), "JobExecution Job-Id cannot be null.");
        Assert.notNull(jobExecution.getStatus(), "JobExecution status cannot be null.");
        Assert.notNull(jobExecution.getCreateTime(), "JobExecution create time cannot be null");
    }

    @Override
    public void updateJobExecution(JobExecution jobExecution) {
        validateJobExecution(jobExecution);
        Assert.notNull(jobExecution.getId(),
                "JobExecution ID cannot be null. JobExecution must be saved before it can be updated");

        Assert.notNull(jobExecution.getVersion(),
                "JobExecution version cannot be null. JobExecution must be saved before it can be updated");

        JobExecution je = getJobExecution(jobExecution.getId());

        if (je == null) {
            throw new NoSuchObjectException("JobExecution " + jobExecution.getJobInstance().getJobName() + " " + jobExecution.getId() + " not found");
        }
        synchronized (jobExecution) {
            AdaptedJobExecution aJobExecution;
            try {
                aJobExecution = adapter.marshal(jobExecution);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }

            XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
            String uri = getUri(jobExecution);
            DocumentDescriptor desc = docMgr.exists(uri);
            if (jobExecution.getVersion() != (int) desc.getVersion()) {
                throw new OptimisticLockingFailureException(uri);
            }

            //Set document metadata
            DocumentMetadataHandle metadata = new DocumentMetadataHandle();
            metadata.withCollections(properties.getCollection(), properties.getJobExecutionCollection());
            JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<AdaptedJobExecution>(jaxbContext());
            handle.set(aJobExecution);
            docMgr.write(desc, metadata, handle);
            desc = docMgr.exists(uri);
            jobExecution.setVersion((int) desc.getVersion());
        }
    }

    @Override
    public List<JobExecution> findJobExecutions(JobInstance jobInstance) {
        String directory = properties.getJobRepositoryDirectory() + "/" + jobInstance.getId() + "/";
        StructuredQueryBuilder qb = new StructuredQueryBuilder(properties.getSearchOptions());
        StructuredQueryDefinition querydef =
                qb.and(
                        qb.directory(true, directory),
                        qb.collection(properties.getJobExecutionCollection())
                );
        QueryManager queryMgr = databaseClient.newQueryManager();
        SearchHandle results = queryMgr.search(querydef, new SearchHandle());
        MatchDocumentSummary[] summaries = results.getMatchResults();
        List<JobExecution> jobExecutions = new ArrayList<JobExecution>();

        DocumentManager docMgr = databaseClient.newXMLDocumentManager();
        for (MatchDocumentSummary summary : summaries) {
            DocumentDescriptor desc = docMgr.exists(summary.getUri());
            JAXBHandle<AdaptedJobExecution> jaxbHandle = new JAXBHandle<AdaptedJobExecution>(jaxbContext());
            summary.getFirstSnippet(jaxbHandle);
            AdaptedJobExecution aJobExecution = jaxbHandle.get();
            try {
                JobExecution jobExecution = adapter.unmarshal(aJobExecution);
                jobExecution.setVersion((int) desc.getVersion());
                jobExecutions.add(jobExecution);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jobExecutions;
    }

    @Override
    public JobExecution getLastJobExecution(JobInstance jobInstance) {
        List<JobExecution> jobExecutions = findJobExecutions(jobInstance);
        int size = jobExecutions.size();
        if (size > 0) {
            return jobExecutions.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Set<JobExecution> findRunningJobExecutions(String jobName) {
        StructuredQueryBuilder qb = new StructuredQueryBuilder(properties.getSearchOptions());
        StructuredQueryDefinition querydef =
                qb.andNot(
                        qb.and(
                                qb.valueConstraint("jobName", jobName),
                                qb.collection("job-execution")
                        ),
                        qb.range(
                                qb.element(new QName(properties.getBatchNamespace(), "endDateTime")),
                                "xs:dateTime",
                                Operator.GT,
                                "1917-01-01T00:00:00.0"
                        )
                );
        logger.info(querydef.serialize());
        QueryManager queryMgr = databaseClient.newQueryManager();
        SearchHandle results = queryMgr.search(querydef, new SearchHandle());
        Set<JobExecution> jobExecutions = new HashSet<JobExecution>();
        for (MatchDocumentSummary summary : results.getMatchResults()) {
            JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<>(jaxbContext());
            summary.getFirstSnippet(handle);
            AdaptedJobExecution aje = handle.get();
            try {
                jobExecutions.add(adapter.unmarshal(aje));
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
        }
        return jobExecutions;
    }

    @Override
    public JobExecution getJobExecution(Long executionId) {
        JobExecution jobExec = null;
        StructuredQueryBuilder qb = new StructuredQueryBuilder(properties.getSearchOptions());
        StructuredQueryDefinition querydef = qb.valueConstraint("id", executionId.toString());
        QueryManager queryMgr = databaseClient.newQueryManager();
        SearchHandle results = queryMgr.search(querydef, new SearchHandle());
        DocumentDescriptor desc;
        if (results.getTotalResults() > 0L) {
            MatchDocumentSummary[] summaries = results.getMatchResults();
            JAXBHandle<AdaptedJobExecution> handle = new JAXBHandle<>(jaxbContext());
            AdaptedJobExecution aje = summaries[0].getFirstSnippet(handle).get();
            try {
                jobExec = adapter.unmarshal(aje);
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
            desc = databaseClient.newDocumentManager().exists(summaries[0].getUri());
            jobExec.setVersion((int) desc.getVersion());
        }

        return jobExec;

    }

    @Override
    public void synchronizeStatus(JobExecution jobExecution) {
        JobExecution je = getJobExecution(jobExecution.getId());
        int currentVersion = je.getVersion();

        if (currentVersion != jobExecution.getVersion()) {
            BatchStatus status = je.getStatus();
            jobExecution.upgradeStatus(status);
            jobExecution.setVersion(currentVersion);
        }
    }

    private String getUri(JobExecution jobExecution) {
        return properties.getJobRepositoryDirectory() + "/" +
                jobExecution.getJobInstance().getInstanceId() + "/" +
                jobExecution.getId() + ".xml";
    }


    protected JAXBContext jaxbContext() {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AdaptedJobExecution.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        return jaxbContext;
    }
}
