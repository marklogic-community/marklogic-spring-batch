package com.marklogic.spring.batch.config;

import com.marklogic.client.spring.BasicConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"file:../core/gradle.properties"})
public class JobTestConfig extends BasicConfig{
}
