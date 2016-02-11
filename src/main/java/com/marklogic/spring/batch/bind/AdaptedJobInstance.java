package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "jobInstance")
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
	public String getId() {
		return Long.toString(id);
	}
	public void setId(String id) {
		this.id = Long.getLong(id);
	}

}
