package com.marklogic.spring.batch.core;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.batch.core.Entity;

@XmlRootElement(name = "jobInstance", namespace=MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE)
public class AdaptedJobInstance extends Entity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String jobName;
	private Long id;
	private String jobParametersKey;
	
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
	
	@XmlElement(name = "id", namespace=MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getJobParametersKey() {
		return jobParametersKey;
	}

	public void setJobParametersKey(String jobParametersKey) {
		this.jobParametersKey = jobParametersKey;
	}

}
