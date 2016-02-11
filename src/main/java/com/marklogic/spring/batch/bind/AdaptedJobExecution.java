package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.batch.core.JobParameters;


@XmlRootElement(name = "job")
public class AdaptedJobExecution {
	
	private JobParameters jobParameters;

	public AdaptedJobExecution() { }
	
	@XmlJavaTypeAdapter(JobParametersAdapter.class)
	public JobParameters getJobParameters() {
		return jobParameters;
	}
	
	public void setJobParameters(JobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}
	
}
