package com.marklogic.spring.batch.core;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "jobInstance", namespace="http://marklogic.com/spring-batch/job-instance")
@XmlType(namespace="http://marklogic.com/spring-batch/job-instance")
public class AdaptedJobInstance {
	
	private String jobName;
	private Long id;
	
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

}
