package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.batch.core.JobExecution;

@XmlRootElement(name = "job")
public class XmlJobExecution {
	private JobExecution jobExecution;
	private XmlJobParameters jobParameters;
	
	public XmlJobExecution() { }
	
	public JobExecution getJobExecution() {
		return jobExecution;
	}
	public void setJobExecution(JobExecution jobExecution) {
		this.jobExecution = jobExecution;
	}
	public XmlJobParameters getJobParameters() {
		return jobParameters;
	}
	public void setJobParameters(XmlJobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}
}
