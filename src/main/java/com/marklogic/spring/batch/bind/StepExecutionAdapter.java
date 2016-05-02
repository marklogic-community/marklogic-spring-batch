package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.StepExecution;

import com.marklogic.spring.batch.core.AdaptedStepExecution;

public class StepExecutionAdapter extends XmlAdapter<AdaptedStepExecution, StepExecution> {

	@Override
	public StepExecution unmarshal(AdaptedStepExecution v) throws Exception {
		StepExecution step = new StepExecution(v.getStepName(), v.getJobExecution());
		step.setId(v.getId());
		step.setStartTime(v.getStartTime());
		return step;
	}

	@Override
	public AdaptedStepExecution marshal(StepExecution v) throws Exception {
		return new AdaptedStepExecution(v);
	}	
	
}