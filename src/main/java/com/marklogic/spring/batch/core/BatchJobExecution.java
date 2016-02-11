package com.marklogic.spring.batch.core;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.marklogic.spring.batch.bind.JobInstanceAdapter;
import com.marklogic.spring.batch.bind.JobParametersAdapter;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;


@XmlRootElement(name = "jobExecution")
public class BatchJobExecution {
	
	public static final String NAMESPACE = "http://projects.spring.io/spring-batch";
	
	private JobParameters jobParameters;
	private JobInstance jobInstance;
	private JobExecution jobExecution;
	private Date createDateTime;

	public BatchJobExecution() { }
	
	public BatchJobExecution(JobExecution jobExecution) {
		this.jobExecution = jobExecution;
		this.jobInstance = jobExecution.getJobInstance();
		this.jobParameters = jobExecution.getJobParameters();
		this.createDateTime = jobExecution.getCreateTime();
	}
	
	@XmlTransient
	public JobExecution getJobExecution() {
		return jobExecution;
	}
	
	public void setJobExecution(JobExecution jobExecution) {
		this.jobExecution = jobExecution;
	}
	
	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	@XmlJavaTypeAdapter(JobInstanceAdapter.class)
	public JobInstance getJobInstance() {
		return jobInstance;
	}

	public void setJobInstance(JobInstance jobInstance) {
		this.jobInstance = jobInstance;
	}
	
	@XmlJavaTypeAdapter(JobParametersAdapter.class)
	public JobParameters getJobParameters() {
		return jobParameters;
	}
	
	public void setJobParameters(JobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}
	
}
