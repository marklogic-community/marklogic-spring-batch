package com.marklogic.spring.batch.test;

import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Collection;

@Configuration
public class SimpleJobLauncherContext {

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        return jobLauncher;
    }

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }

    @Bean
    public JobRepositoryFactoryBean jobRepository() {
        return new JobRepositoryFactoryBean();
    }

    @Bean
    @Lazy
    public MapJobRepositoryFactoryBean mapJobRepository() {
        return new MapJobRepositoryFactoryBean();
    }

    @Bean
    public JobOperator jobOperator(
            JobLauncher jobLauncher,
            JobExplorer jobExplorer,
            JobRepository jobRepository,
            JobRegistry jobRegistry) {
        SimpleJobOperator simpleJobOperator = new SimpleJobOperator();
        simpleJobOperator.setJobLauncher(jobLauncher);
        simpleJobOperator.setJobExplorer(jobExplorer);
        simpleJobOperator.setJobRepository(jobRepository);
        simpleJobOperator.setJobRegistry(jobRegistry);
        return simpleJobOperator;
    }

    @Bean
    public JobExplorer jobExplorer() {
        //SimpleJobExplorer jobExplorer = new SimpleJobExplorer();
        SimpleJobExplorer jobExplorer = null;
        return jobExplorer;

    }

	@Bean
    public JobRegistry jobRegistry() {
        MapJobRegistry jobRegistry = new MapJobRegistry();
        return jobRegistry;
    }

}
