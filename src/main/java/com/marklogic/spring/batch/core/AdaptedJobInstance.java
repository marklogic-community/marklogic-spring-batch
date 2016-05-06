package com.marklogic.spring.batch.core;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;

@XmlRootElement(name = "jobInstance", namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
public class AdaptedJobInstance {
	
	private Long id;
	private Integer version = new Integer(0);
	private String jobName;
	
	public AdaptedJobInstance() { }
	
	public AdaptedJobInstance(JobInstance jobInstance) {
		this.id = jobInstance.getId();
		if (jobInstance.getVersion() == null) {
			jobInstance.setVersion(new Integer(0));
		} else {
			this.setVersion(jobInstance.getVersion());
		}
		this.jobName = jobInstance.getJobName();
	}
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	public void updateJobExecution(JobExecution jobExecution) {
		return;
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
