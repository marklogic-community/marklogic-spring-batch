package com.marklogic.spring.batch.core.repository.dao;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentPage;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.spring.batch.bind.ExecutionContextAdapter;
import com.marklogic.spring.batch.config.BatchProperties;
import com.marklogic.spring.batch.core.AdaptedExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.Collection;

public class MarkLogicExecutionContextDao implements ExecutionContextDao {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private JobExecutionDao jobExecutionDao;
    private StepExecutionDao stepExecutionDao;
    private DatabaseClient databaseClient;
    private BatchProperties properties;
    private ExecutionContextAdapter adapter;

    public MarkLogicExecutionContextDao(DatabaseClient databaseClient, BatchProperties batchProperties) {
        this.properties = batchProperties;
        this.databaseClient = databaseClient;
        adapter = new ExecutionContextAdapter();
    }

    @Override
    public ExecutionContext getExecutionContext(JobExecution jobExecution) {
        return getExecutionContext(getUri(jobExecution));
    }

    @Override
    public ExecutionContext getExecutionContext(StepExecution stepExecution) {
        return getExecutionContext(getUri(stepExecution));
    }

    private ExecutionContext getExecutionContext(String uri) {
        XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
        JAXBHandle<AdaptedExecutionContext> handle = new JAXBHandle<AdaptedExecutionContext>(jaxbContext());
        DocumentPage page = docMgr.read(uri);
        AdaptedExecutionContext aec = page.next().getContent(handle).get();
        ExecutionContext ec = null;
        try {
            ec = adapter.unmarshal(aec);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return ec;
    }

    @Override
    public void saveExecutionContext(JobExecution jobExecution) {
        saveExecutionContext(getUri(jobExecution), jobExecution.getExecutionContext());
    }

    @Override
    public void saveExecutionContext(StepExecution stepExecution) {
        saveExecutionContext(getUri(stepExecution), stepExecution.getExecutionContext());

    }

    private void saveExecutionContext(String uri, ExecutionContext ec) {
        XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();

        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections(properties.getCollection(), properties.getExecutionContextCollection());

        JAXBHandle<AdaptedExecutionContext> handle = new JAXBHandle<AdaptedExecutionContext>(jaxbContext());
        AdaptedExecutionContext aec = null;
        try {
            aec = adapter.marshal(ec);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        handle.set(aec);
        DocumentDescriptor desc = docMgr.exists(uri);
        if (desc == null) {
            desc = docMgr.newDescriptor(uri);
        }
        docMgr.write(desc, metadata, handle);
    }

    @Override
    public void saveExecutionContexts(Collection<StepExecution> stepExecutions) {
        Assert.notNull(stepExecutions, "Attempt to save a null collection of step executions");
        for (StepExecution stepExec : stepExecutions) {
            saveExecutionContext(stepExec);
        }
    }

    @Override
    public void updateExecutionContext(JobExecution jobExecution) {
        saveExecutionContext(getUri(jobExecution), jobExecution.getExecutionContext());

    }

    @Override
    public void updateExecutionContext(StepExecution stepExecution) {
        saveExecutionContext(getUri(stepExecution), stepExecution.getExecutionContext());
    }

    private String getUri(JobExecution jobExecution) {
        return properties.getJobRepositoryDirectory() + "/" +
                jobExecution.getJobInstance().getId() + "/" +
                jobExecution.getId() + "/execution-context.xml";
    }

    private String getUri(StepExecution stepExecution) {
        return properties.getJobRepositoryDirectory() + "/" +
                stepExecution.getJobExecution().getJobInstance().getId() + "/" +
                stepExecution.getJobExecution().getId() + "/" +
                stepExecution.getId() + "/execution-context.xml";
    }

    protected JAXBContext jaxbContext() {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(AdaptedExecutionContext.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
        return jaxbContext;
    }

}
