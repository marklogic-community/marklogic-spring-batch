package com.marklogic.client.spring.batch;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan( 
	basePackageClasses = {ApplicationConfiguration.class, com.marklogic.client.spring.DatabaseConfig.class },
	excludeFilters = @ComponentScan.Filter(value = com.marklogic.client.spring.BasicConfig.class, type = FilterType.ASSIGNABLE_TYPE)
)
public class ApplicationConfiguration {
}