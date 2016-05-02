package com.marklogic.spring.batch.core.repository.dao;

import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.util.Assert;
import org.w3c.dom.Document;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.spring.batch.bind.StepExecutionAdapter;
import com.marklogic.spring.batch.core.AdaptedStepExecution;

public class MarkLogicStepExecutionDao extends AbstractMarkLogicBatchMetadataDao implements StepExecutionDao {
	
	private static final Log logger = LogFactory.getLog(MarkLogicJobInstanceDao.class);
	
	public MarkLogicStepExecutionDao(DatabaseClient databaseClient) {
		this.databaseClient = databaseClient;
	}

	@Override
	public void saveStepExecution(StepExecution stepExecution) {
		Assert.isTrue(stepExecution.getId() == null);
		Assert.isTrue(stepExecution.getVersion() == null);
		Assert.notNull(stepExecution.getJobExecutionId(), "JobExecution must be saved already.");
		validateStepExecution(stepExecution);
		
		XMLDocumentManager xmlDocMgr = databaseClient.newXMLDocumentManager();
		stepExecution.setId(incrementer.nextLongValue());
		stepExecution.incrementVersion();
		
		String uri = SPRING_BATCH_DIR + "step-execution/" + stepExecution.getId().toString() + ".xml";
		DocumentDescriptor desc = xmlDocMgr.exists(uri);
		if (desc == null) {
			desc = xmlDocMgr.newDescriptor(uri);
		}
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder;
        Document doc = null;
		try {
			documentBuilder = domFactory.newDocumentBuilder();
			doc = documentBuilder.newDocument();
	        Marshaller marshaller = jaxbContext().createMarshaller();
	        StepExecutionAdapter adapter = new StepExecutionAdapter();
	        AdaptedStepExecution ase  = adapter.marshal(stepExecution);
	        marshaller.marshal(ase, doc);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
        DOMHandle handle = new DOMHandle();
        handle.set(doc);      
      
        //Set document metadata
        DocumentMetadataHandle jobInstanceMetadata = new DocumentMetadataHandle();
        jobInstanceMetadata.getCollections().add(COLLECTION_STEP_EXECUTION);
        
		xmlDocMgr.write(desc, jobInstanceMetadata, handle);
		logger.info("insert:" + uri + "," + desc.getVersion());
		
    	return;

	}

	@Override
	public void saveStepExecutions(Collection<StepExecution> stepExecutions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateStepExecution(StepExecution stepExecution) {
		// TODO Auto-generated method stub

	}

	@Override
	public StepExecution getStepExecution(JobExecution jobExecution, Long stepExecutionId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addStepExecutions(JobExecution jobExecution) {
		// TODO Auto-generated method stub

	}
	
	private void validateStepExecution(StepExecution stepExecution) {
		Assert.notNull(stepExecution);
		Assert.notNull(stepExecution.getStepName(), "StepExecution step name cannot be null.");
		Assert.notNull(stepExecution.getStartTime(), "StepExecution start time cannot be null.");
		Assert.notNull(stepExecution.getStatus(), "StepExecution status cannot be null.");
	}

	protected JAXBContext jaxbContext() {
		JAXBContext jaxbContext = null;
		try {
            jaxbContext = JAXBContext.newInstance(AdaptedStepExecution.class);
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
		return jaxbContext;
	}
}
