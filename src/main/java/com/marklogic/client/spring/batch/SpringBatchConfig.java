package com.marklogic.client.spring.batch;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.input.DOMBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class SpringBatchConfig {
	
	@Bean
	public JAXBContext jaxbContext() throws JAXBException {
		return JAXBContext.newInstance(org.geonames.Geoname.class);
	}
		
	@Bean
	public DocumentBuilder documentBuilder() {
		DocumentBuilder docBuilder = null;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true);
			docBuilder = domFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return docBuilder;
	}
	
	@Bean
	public DOMBuilder domBuilder() {
		return new DOMBuilder();
	}
	
	@Bean
	protected TaskExecutor taskExecutor() {
		//CustomizableThreadFactory tf = new CustomizableThreadFactory("geoname-threads");
		//SimpleAsyncTaskExecutor sate =  new SimpleAsyncTaskExecutor(tf);
		//sate.setConcurrencyLimit(8);
		SyncTaskExecutor ste = new SyncTaskExecutor();
		return ste;
	}
	
	@Bean
	public JobRepository jobRepository() {
		MapJobRepositoryFactoryBean mapJobRepositoryFactoryBean = new MapJobRepositoryFactoryBean();
	    mapJobRepositoryFactoryBean.setTransactionManager(platformTransactionManager());
	    JobRepository jobRepo = null;
	    try {
			jobRepo = mapJobRepositoryFactoryBean.getObject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return jobRepo;
	}	
	
	@Bean
	public JobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		launcher.setTaskExecutor(taskExecutor());
		return launcher;
	}
	
	@Bean
	public JobRegistry jobRegistry() {
		return new MapJobRegistry();
	}
	
	@Bean
	public PlatformTransactionManager platformTransactionManager() {
		return new ResourcelessTransactionManager();
	}
	
	@Bean
	public JobBuilderFactory jobBuilderFactory(JobRepository jobRepository) {
		return new JobBuilderFactory(jobRepository);
	}
	
	@Bean
	public StepBuilderFactory stepBuilderFactory(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
		return new StepBuilderFactory(jobRepository, platformTransactionManager);
	}
	
	@Bean
	public JobLauncher jobLauncher() {
		return new SimpleJobLauncher();
	}
	
	
}
