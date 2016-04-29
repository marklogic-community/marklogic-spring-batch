package com.marklogic.spring.batch.core.repository.dao;

import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.beans.factory.InitializingBean;
import org.w3c.dom.Document;

import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.core.AdaptedJobExecution;
import com.marklogic.spring.batch.core.AdaptedJobInstance;
import com.marklogic.spring.batch.core.AdaptedJobParameters;
import com.marklogic.spring.batch.core.AdaptedStepExecution;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

public class MarkLogicJobExecutionDao extends AbstractMarkLogicBatchMetadataDao implements JobExecutionDao, InitializingBean {
	
	private static final Log logger = LogFactory.getLog(MarkLogicJobExecutionDao.class);
	
	public MarkLogicJobExecutionDao() {
	}


	@Override
	public void saveJobExecution(JobExecution jobExecution) {
		if (jobExecution.getId() == null) {
			jobExecution.setId(generateId());
		}
		try {
        	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = domFactory.newDocumentBuilder();
        	Document doc = documentBuilder.newDocument();
            Marshaller marshaller = jaxbContext().createMarshaller();
            JobExecutionAdapter adapter = new JobExecutionAdapter();
            AdaptedJobExecution aje = adapter.marshal(jobExecution);
            marshaller.marshal(aje, doc);
            DOMHandle handle = new DOMHandle();
            handle.set(doc);
            DocumentMetadataHandle jobExecutionMetadata = new DocumentMetadataHandle();
            jobExecutionMetadata.getCollections().add(MarkLogicSpringBatch.COLLECTION_JOB_EXECUTION);
            XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
            xmlDocMgr.write(MarkLogicSpringBatch.SPRING_BATCH_DIR + jobExecution.getId().toString() + ".xml", jobExecutionMetadata, handle);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }catch (Exception e) {
        	e.printStackTrace();
        }

	}

	@Override
	public void updateJobExecution(JobExecution jobExecution) {
		saveJobExecution(jobExecution);

	}

	@Override
	public List<JobExecution> findJobExecutions(JobInstance jobInstance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobExecution getLastJobExecution(JobInstance jobInstance) {
		logger.debug("");
		return null;
	}

	@Override
	public Set<JobExecution> findRunningJobExecutions(String jobName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobExecution getJobExecution(Long executionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void synchronizeStatus(JobExecution jobExecution) {
		// TODO Auto-generated method stub

	}
	
	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext = null;
		try {
            jaxbContext = JAXBContext.newInstance(AdaptedJobExecution.class, AdaptedJobInstance.class, AdaptedJobParameters.class, AdaptedStepExecution.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
		return jaxbContext;
	}

}
