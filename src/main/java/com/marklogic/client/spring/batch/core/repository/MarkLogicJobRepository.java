package com.marklogic.client.spring.batch.core.repository;

import java.util.Collection;
import java.util.Random;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;

public class MarkLogicJobRepository implements JobRepository, MarkLogicSpringBatchRepository {

    private DocumentBuilder documentBuilder;

    private JAXBContext jaxbContext;
    private DocumentMetadataHandle jobExecutionMetadata;
    private DocumentMetadataHandle jobInstanceMetadata;
    private DatabaseClient client;

    public MarkLogicJobRepository(DatabaseClient client) {
        this.client = client;
        initializeDocumentBuilder();
        try {
            jaxbContext = JAXBContext.newInstance(org.springframework.batch.core.JobExecution.class,
                    org.springframework.batch.core.JobInstance.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        jobExecutionMetadata = new DocumentMetadataHandle();
        jobExecutionMetadata.getCollections().add(COLLECTION_JOB_EXECUTION);

        jobInstanceMetadata = new DocumentMetadataHandle();
        jobInstanceMetadata.getCollections().add(COLLETION_JOB_INSTANCE);
    }

    protected void initializeDocumentBuilder() {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        try {
            this.documentBuilder = domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean isJobInstanceExists(String jobName, JobParameters jobParameters) {

        return false;
    }

    @Override
    public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
        long id = getRandomNumber();
        JobInstance jobInstance = new JobInstance(id, jobName);

        Document doc = documentBuilder.newDocument();
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(jobInstance, doc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
        DOMHandle handle = new DOMHandle();
        handle.set(doc);
        xmlDocMgr.write(SPRING_BATCH_DIR + "/job-instance/" + id, jobInstanceMetadata, handle);
        return jobInstance;
    }

    @Override
    public JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters,
            String jobConfigurationLocation) {
        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters, jobConfigurationLocation);
        Document doc = documentBuilder.newDocument();
        Marshaller marshaller = null;
        try {
            marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(jobExecution, doc);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
        DOMHandle handle = new DOMHandle();
        handle.set(doc);
        xmlDocMgr.write("/tes123t.xml", handle);

        return jobExecution;
    }

    @Override
    public JobExecution createJobExecution(String jobName, JobParameters jobParameters)
            throws JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        // Create a JobExecution instance

        JobInstance jobInstance = new JobInstance(getRandomNumber(), jobName);
        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);
        jobExecution.setId(getRandomNumber());

        Document doc = documentBuilder.newDocument();
        Marshaller marshaller = null;
        try {
            JAXBElement<JobExecution> jaxbElement = new JAXBElement<JobExecution>(
                    new QName("http://marklogic.com/spring-batch", "jobExecution"),
                    org.springframework.batch.core.JobExecution.class, jobExecution);
            marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(jaxbElement, doc);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        XMLDocumentManager xmlDocMgr = client.newXMLDocumentManager();
        DOMHandle handle = new DOMHandle();
        handle.set(doc);
        xmlDocMgr.write(SPRING_BATCH_DIR + "/job-execution/" + jobExecution.getId().toString(), jobExecutionMetadata,
                handle);

        return jobExecution;
    }

    @Override
    public void update(JobExecution jobExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public void add(StepExecution stepExecution) {

    }

    @Override
    public void addAll(Collection<StepExecution> stepExecutions) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(StepExecution stepExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateExecutionContext(StepExecution stepExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateExecutionContext(JobExecution jobExecution) {
        // TODO Auto-generated method stub

    }

    @Override
    public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getStepExecutionCount(JobInstance jobInstance, String stepName) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public JobExecution getLastJobExecution(String jobName, JobParameters jobParameters) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getRandomNumber() {
        long LOWER_RANGE = 0; // assign lower range value
        long UPPER_RANGE = 9999999; // assign upper range value
        Random random = new Random();
        return LOWER_RANGE + (long) (random.nextDouble() * (UPPER_RANGE - LOWER_RANGE));
    }

}
