package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.StepExecution;

import com.marklogic.spring.batch.core.AdaptedStepExecution;

public class StepExecutionAdapter extends XmlAdapter<AdaptedStepExecution, StepExecution> {

	@Override
	public StepExecution unmarshal(AdaptedStepExecution v) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdaptedStepExecution marshal(StepExecution v) throws Exception {
		AdaptedStepExecution adaptedStep = new AdaptedStepExecution();
		adaptedStep.setStepName(v.getStepName());
		return adaptedStep;
	}



	
	
	
}