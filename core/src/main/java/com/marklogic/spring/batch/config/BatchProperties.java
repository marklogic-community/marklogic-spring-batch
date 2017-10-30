package com.marklogic.spring.batch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatchProperties {

    private String jobRepostioryDirectory;
    private String collection;
    private String searchOptions;

    public String getJobRepositoryDirectory() {
        return jobRepostioryDirectory;
    }

    public String getCollection() {
        return collection;
    }

    public String getSearchOptions() {
        return searchOptions;
    }

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
    public void setSpringBatchCollection(
            @Value("${batch.jobRepo.collection:batch}") String collection) {
        this.collection = collection;
    }




}
