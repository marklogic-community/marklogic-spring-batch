package com.marklogic.client.spring.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@EnableAutoConfiguration
@ComponentScan(
	basePackageClasses = { com.marklogic.client.spring.batch.SpringBatchConfig.class, com.marklogic.client.spring.DatabaseConfig.class },
	excludeFilters = @ComponentScan.Filter(value = com.marklogic.client.spring.BasicConfig.class, type = FilterType.ASSIGNABLE_TYPE)
)
public class Application {
	
	private static Log log = LogFactory.getLog(Application.class);
	
	public static void main(String[] args) {
    	log.debug("Starting...");
        SpringApplication.run(Application.class, args);
        log.debug("Ending...");
    }
    

}