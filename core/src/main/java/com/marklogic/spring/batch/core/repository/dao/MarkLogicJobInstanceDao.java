package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.*;
import com.marklogic.spring.batch.bind.JobInstanceAdapter;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MarkLogicJobInstanceDao implements JobInstanceDao {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private JobKeyGenerator<JobParameters> jobKeyGenerator = new DefaultJobKeyGenerator();

    private DatabaseClient databaseClient;
    private BatchProperties properties;
    private JobInstanceAdapter adapter;

    public MarkLogicJobInstanceDao(DatabaseClient databaseClient, BatchProperties batchProperties) {
        this.databaseClient = databaseClient;
        this.properties = batchProperties;
    }

    @Override
    public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
        validateJobInstanceParameters(jobName, jobParameters);

        Assert.state(getJobInstance(jobName, jobParameters) == null,
                "JobInstance must not already exist");

        JobInstance jobInstance = new JobInstance(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE), jobName);
        jobInstance.incrementVersion();

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder;
        Document doc = null;
        try {
            documentBuilder = domFactory.newDocumentBuilder();
            doc = documentBuilder.newDocument();
            JAXBContext jc = JAXBContext.newInstance(AdaptedJobInstance.class);
            Marshaller marshaller = jc.createMarshaller();
            JobInstanceAdapter adapter = new JobInstanceAdapter(jobParameters);
            AdaptedJobInstance aji = adapter.marshal(jobInstance);
            aji.setCreateDateTime(new Date());
            marshaller.marshal(aji, doc);
        } catch (ParserConfigurationException | JAXBException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        DOMHandle handle = new DOMHandle();
        handle.set(doc);

        XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
        String uri = getUri(jobInstance.getInstanceId());
        //Set document metadata
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections(properties.getCollection(), properties.getJobInstanceCollection());
        xmlDocMgr.write(uri, metadata, handle);
        //xmlDocMgr.write(uri, handle);
        logger.trace("jobInstance: " + uri);

        return jobInstance;
    }

    @Override
    public JobInstance getJobInstance(String jobName, JobParameters jobParameters) {
        validateJobInstanceParameters(jobName, jobParameters);

        StructuredQueryBuilder qb = new StructuredQueryBuilder(properties.getSearchOptions());
        StructuredQueryDefinition querydef =
                qb.and(
                        qb.valueConstraint("jobKey", jobKeyGenerator.generateKey(jobParameters)),
                        qb.valueConstraint("jobName", jobName),
                        qb.collection(properties.getJobInstanceCollection())
                );
        QueryManager queryMgr = databaseClient.newQueryManager();
        SearchHandle results = queryMgr.search(querydef, new SearchHandle());

        List<JobInstance> jobInstances = new ArrayList<>();
        MatchDocumentSummary[] summaries = results.getMatchResults();
        AdaptedJobInstance jobInstance;
        JobInstanceAdapter adapter = new JobInstanceAdapter(jobParameters);
        for (MatchDocumentSummary summary : summaries) {
            JAXBHandle<AdaptedJobInstance> jaxbHandle = new JAXBHandle<AdaptedJobInstance>(jaxbContext());
            summary.getFirstSnippet(jaxbHandle);
            jobInstance = jaxbHandle.get();
            try {
                jobInstances.add(adapter.unmarshal(jobInstance));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (jobInstances.size() == 0) {
            return null;
        } else {
            return jobInstances.get(0);
        }

    }

    @Override
    public JobInstance getJobInstance(Long instanceId) {
        XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
        JobInstance jobInstance = null;
        try {
            JAXBHandle<AdaptedJobInstance> jaxbHandle = xmlDocMgr.read(getUri(instanceId),
                    new JAXBHandle<AdaptedJobInstance>(jaxbContext()));
            jobInstance = new JobInstanceAdapter().unmarshal(jaxbHandle.get());
        } catch (ResourceNotFoundException ex) {
            return null;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error(ex.getStackTrace().toString());
        }
        return jobInstance;
    }

    @Override
    public JobInstance getJobInstance(JobExecution jobExecution) {
        return getJobInstance(jobExecution.getJobInstance().getId());
    }

    @Override
    public List<JobInstance> getJobInstances(String jobName, int start, int count) {
        QueryManager queryMgr = databaseClient.newQueryManager();
        StringQueryDefinition querydef = queryMgr.newStringDefinition(properties.getSearchOptions());
        querydef.setCriteria("jobName:" + jobName + " AND sort:date AND type:job-instance");
        logger.debug(querydef.getCriteria());
        SearchHandle results = queryMgr.search(querydef, new SearchHandle());
        List<JobInstance> jobInstances = new ArrayList<>();
        MatchDocumentSummary[] summaries = results.getMatchResults();
        JobInstance jobInstance;
        if (start + count > summaries.length) {
            return jobInstances;
        }
        for (int i = start; i < start + count; i++) {
            JAXBHandle<AdaptedJobInstance> jaxbHandle = new JAXBHandle<>(jaxbContext());
            summaries[i].getFirstSnippet(jaxbHandle);
            AdaptedJobInstance aji = jaxbHandle.get();
            JobInstanceAdapter adapter = new JobInstanceAdapter();
            try {
                jobInstances.add(adapter.unmarshal(aji));
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error(ex.getMessage());
            }
        }
        return jobInstances;
    }

    @Override
    public List<String> getJobNames() {
        List<String> jobNames = new ArrayList<>();
        QueryManager queryMgr = databaseClient.newQueryManager();
        ValuesDefinition valuesDef = queryMgr.newValuesDefinition("jobName", properties.getSearchOptions());
        ValuesHandle results = queryMgr.values(valuesDef, new ValuesHandle());
        for (CountedDistinctValue value : results.getValues()) {
            jobNames.add(value.get("xs:string", String.class));
        }
        return jobNames;
    }

    @Override
    public List<JobInstance> findJobInstancesByName(String jobName, int start, int count) {
        List<JobInstance> jobInstances = new ArrayList<>();
        QueryManager queryMgr = databaseClient.newQueryManager();
        StructuredQueryBuilder qb = new StructuredQueryBuilder(properties.getSearchOptions());
        StructuredQueryDefinition querydef =
                qb.and(
                        qb.valueConstraint("jobName", jobName),
                        qb.collection(properties.getJobInstanceCollection())
                );
        queryMgr.setPageLength((long) count);
        SearchHandle results = queryMgr.search(querydef, new SearchHandle(), start);
        MatchDocumentSummary[] summaries = results.getMatchResults();

        for (MatchDocumentSummary summary : summaries) {
            JAXBHandle<AdaptedJobInstance> jaxbHandle = new JAXBHandle<AdaptedJobInstance>(jaxbContext());
            summary.getFirstSnippet(jaxbHandle);
            AdaptedJobInstance aji = jaxbHandle.get();
            JobInstance jobInstance = null;
            try {
                jobInstance = adapter.unmarshal(aji);
            } catch (Exception e) {
                e.printStackTrace();
            }
            jobInstances.add(jobInstance);
        }
        return jobInstances;
    }

    @Override
    public int getJobInstanceCount(String jobName) throws NoSuchJobException {
        StructuredQueryBuilder qb = new StructuredQueryBuilder(properties.getSearchOptions());
        StructuredQueryDefinition querydef =
                qb.and(
                        qb.valueConstraint("jobName", jobName),
                        qb.collection(properties.getJobInstanceCollection())
                );
        QueryManager queryMgr = databaseClient.newQueryManager();
        SearchHandle results = queryMgr.search(querydef, new SearchHandle());
        int count = (int) results.getTotalResults();
        if (count == 0) {
            throw new NoSuchJobException(jobName + " not found");
        } else {
            return count;
        }
    }


    protected JAXBContext jaxbContext() {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AdaptedJobInstance.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        return jaxbContext;
    }

    private void validateJobInstanceParameters(String jobName, JobParameters jobParameters) {
        Assert.notNull(jobName, "Job name must not be null.");
        Assert.notNull(jobParameters, "JobParameters must not be null.");
    }

    private String getUri(long jobInstanceId) {
        return properties.getJobRepositoryDirectory() + "/" +
                jobInstanceId + "/" +
                jobInstanceId + ".xml";
    }


}
