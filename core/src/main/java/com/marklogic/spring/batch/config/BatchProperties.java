package com.marklogic.spring.batch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatchProperties {

    private String jobRepostioryDirectory;
    private String collection;
    private String jobInstanceCollection;
    private String jobExecutionCollection;
    private String searchOptions;
    private String batchNamespace;

    public String getBatchNamespace() {
        return batchNamespace;
    }

    public String getJobRepositoryDirectory() {
        return jobRepostioryDirectory;
    }

    public String getCollection() {
        return collection;
    }

    public String getSearchOptions() {
        return searchOptions;
    }

    public String getJobInstanceCollection() { return jobInstanceCollection; }

    public String getJobExecutionCollection() { return jobExecutionCollection; }

    @Autowired
    public void setSearchOptions(
            @Value("${batch.jobRepo.searchOptions:spring-batch}") String searchOptions) {
        this.searchOptions = searchOptions;
    }

    @Autowired
    public void setJobRepositoryDirectory(
            @Value("${batch.jobRepo.directory:/batch}") String jobRepositoryDirectory) {
        this.jobRepostioryDirectory = jobRepositoryDirectory;
    }

    @Autowired
    public void setCollection(
            @Value("${batch.jobRepo.collection:batch}") String collection) {
        this.collection = collection;
    }

    @Autowired
    public void setJobInstanceCollection(
            @Value("${batch.jobRepo.jobInstanceCollection:job-instance}") String collection) {
        this.jobInstanceCollection = collection;
    }

    @Autowired
    public void setJobExecutionCollection(
            @Value("${batch.jobRepo.jobExecutionCollection:job-execution}") String collection) {
        this.jobExecutionCollection = collection;
    }

    @Autowired
    public void setBatchNamespace(
            @Value("${batch.jobRepo.namespace:http://marklogic.com/spring-batch}") String batchNamespace) {
        this.batchNamespace = batchNamespace;
    }




}
