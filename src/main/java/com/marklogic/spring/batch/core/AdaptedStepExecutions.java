package com.marklogic.spring.batch.core;

import java.util.Collection;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

@XmlRootElement(name = "stepExecutions")
public class AdaptedStepExecutions {
	
	private Collection<AdaptedStepExecution> adaptedStepExecutions;
	
	private JobExecution jobExecution;
	
	public AdaptedStepExecutions() { }
	
	public AdaptedStepExecutions(Collection<StepExecution> coll, JobExecution jobExecution) {
		this.jobExecution = jobExecution;
		adaptedStepExecutions = new ArrayList<AdaptedStepExecution>();
		for (StepExecution step : coll) {
			AdaptedStepExecution ase = new AdaptedStepExecution();
			ase.stepName = step.getStepName();
			adaptedStepExecutions.add(ase);
		}
	}
    
    @XmlElement(name="stepExecution")
	public Collection<AdaptedStepExecution> getStepExecutions() {
		return adaptedStepExecutions;
	}
    
    public Collection<AdaptedStepExecution> getAdaptedStepExecutions() {
		return adaptedStepExecutions;
	}

	public void setAdaptedStepExecutions(Collection<AdaptedStepExecution> adaptedStepExecutions) {
		this.adaptedStepExecutions = adaptedStepExecutions;
	}

	public JobExecution getJobExecution() {
		return jobExecution;
	}

	public void setJobExecution(JobExecution jobExecution) {
		this.jobExecution = jobExecution;
	}

	public static class AdaptedStepExecution {
        @XmlValue 
        public String stepName;
    }
	
}
