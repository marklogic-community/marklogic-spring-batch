package com.marklogic.spring.batch.core;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "jobInstance", namespace=MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE)
public class AdaptedJobInstance {
	
	private String jobName;
	private Long id;
	private String jobKey;
	
	public AdaptedJobInstance() { }
	
	public AdaptedJobInstance(Long id, String jobName) {
		this.id = id;
		this.jobName = jobName;
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

	public String getJobKey() {
		return jobKey;
	}

	public void setJobKey(String jobKey) {
		this.jobKey = jobKey;
	}

}
