package com.marklogic.spring.batch.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

import com.marklogic.spring.batch.bind.JobExecutionAdapter;
import com.marklogic.spring.batch.bind.JobParametersAdapter;


@XmlRootElement(name = "jobInstance", namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
public class AdaptedJobInstance {
	
	private Long id;
	private Integer version;
	private Date createDateTime;
	private String jobName;
	private String jobKey;
	private JobParameters jobParameters;
	private List<JobExecution> jobExecutions;
	
	public AdaptedJobInstance() { }
	
	public AdaptedJobInstance(JobInstance jobInstance) {
		this.setId(jobInstance.getId());
		this.setVersion(jobInstance.getVersion());
		this.jobName = jobInstance.getJobName();
		jobExecutions = new ArrayList<JobExecution>();
	}
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
	
	@XmlJavaTypeAdapter(JobParametersAdapter.class)
	@XmlElement(name = "jobParameters", namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
	public JobParameters getJobParameters() {
		return jobParameters;
	}

	public void setJobParameters(JobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}

	@XmlJavaTypeAdapter(JobExecutionAdapter.class)
	@XmlElement(name = "jobExecution", namespace=MarkLogicSpringBatch.JOB_EXECUTION_NAMESPACE)
	public List<JobExecution> getJobExecutions() {
		return jobExecutions;
	}

	public void setJobExecutions(List<JobExecution> jobExecutions) {
		this.jobExecutions = jobExecutions;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

}
