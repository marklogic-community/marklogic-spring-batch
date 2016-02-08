package com.marklogic.spring.batch.config;

import javax.xml.bind.JAXBContext;

import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.core.repository.MarkLogicJobRepository;

@ActiveProfiles("marklogic")
@Configuration
public class MarkLogicSpringBatchConfig {
	
	private JAXBContext jaxbContext;
	
	@Autowired
	private DatabaseClientProvider databaseClientProvider;

	@Bean
	public JAXBContext jaxbContext() throws Exception {
		jaxbContext = JAXBContext.newInstance(org.springframework.batch.core.JobExecution.class,
                    org.springframework.batch.core.JobInstance.class);
		return jaxbContext;
	}	
	
	@Bean
	public JobRepository jobRepository() {
		return new MarkLogicJobRepository(databaseClientProvider.getDatabaseClient());
	}

}
