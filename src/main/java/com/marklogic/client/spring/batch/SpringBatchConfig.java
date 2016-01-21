package com.marklogic.client.spring.batch;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.input.DOMBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.batch.core.explore.MarkLogicJobExplorer;
import com.marklogic.client.spring.batch.core.repository.MarkLogicJobRepository;

@Configuration
public class SpringBatchConfig {
	
	@Autowired
	DatabaseClientProvider databaseClientProvider;
		
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
	public JobBuilder jobBuilder(JobRepository jobRepository) {
		JobBuilder jobBuilder = new JobBuilder("marklogic-jobs");
		return jobBuilder.repository(jobRepository);
	}
	
	@Bean
	public JobRepository jobRepository() {
		return new MarkLogicJobRepository(databaseClientProvider.getDatabaseClient());
	}
	
	@Bean
	public JobExplorer jobExplorer() {
		return new MarkLogicJobExplorer(databaseClientProvider.getDatabaseClient());
	}
	
}
