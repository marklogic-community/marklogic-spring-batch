package com.marklogic.client.spring.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Application {
 
    @Autowired
    JobLauncher jobLauncher;
    
    @Autowired
    JobRepository jobRepository;
 
    @Autowired
    @Qualifier("loadGeonamesJob")
    Job loadGeonames;
    
    @Autowired
    @Qualifier("corbJob")
    Job corbJob;
 
    public static void main(String... args) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, 
    		JobRestartException, JobInstanceAlreadyCompleteException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);
        Application main = context.getBean(Application.class);
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("uris", "/ext/uris.xqy");
        jobParametersBuilder.addString("process", "/ext/process.xqy");        
        main.jobLauncher.run(main.corbJob, jobParametersBuilder.toJobParameters());
        context.close();
 
    }
 
}