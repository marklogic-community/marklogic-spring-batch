package com.marklogic.spring.batch.test;

import org.springframework.context.annotation.PropertySource;

@PropertySource(value = "classpath:gradle.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:job.properties", ignoreResourceNotFound = true)
public class DataSourceContext {


}
