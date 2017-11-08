package com.marklogic.spring.batch.core;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobKeyGenerator;
import org.springframework.batch.core.JobParameters;

import java.util.Date;

@XmlRootElement(name = "jobInstance", namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
public class AdaptedJobInstance {
	
	private Long id;
	private Integer version = 0;
	private String jobName;
	private String jobKey;
	private Date createDateTime;

	private JobKeyGenerator<JobParameters> jobKeyGenerator = new DefaultJobKeyGenerator();
	
	public AdaptedJobInstance() { }
	
	public AdaptedJobInstance(JobInstance jobInstance) {
		this.id = jobInstance.getId();
		if (jobInstance.getVersion() == null) {
			jobInstance.setVersion(0);
		} else {
			this.setVersion(jobInstance.getVersion());
		}
		this.jobName = jobInstance.getJobName();
	}

	public AdaptedJobInstance(JobInstance jobInstance, JobParameters jobParameters) {
		this(jobInstance);
		setJobKey(jobKeyGenerator.generateKey(jobParameters));
	}
	
	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
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

	public String getJobKey() { return jobKey; }

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

	public void setJobKey(JobParameters jobParameters) {
		this.jobKey = jobKeyGenerator.generateKey(jobParameters);
	}

	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}
}
