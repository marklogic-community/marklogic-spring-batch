package com.marklogic.spring.batch.test;

import org.springframework.batch.test.JobLauncherTestUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobRunnerContext {

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();

    }
}
