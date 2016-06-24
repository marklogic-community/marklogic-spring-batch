package com.marklogic.spring.batch.test;

import com.marklogic.client.spring.BasicConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * This config is only intended to be used by the jobs projects in this repository. It assumes a path to the core
 * project's gradle.properties file. If that path works for you in your own project, feel free to use this.
 */
@Configuration
@PropertySource({"file:../../msb-core/gradle.properties"})
public class JobProjectTestConfig extends BasicConfig {
}
