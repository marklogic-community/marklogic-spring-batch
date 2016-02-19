package com.marklogic.spring.batch.bind;

import java.util.Collection;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.StepExecution;

import com.marklogic.spring.batch.core.AdaptedStepExecutions;

public class StepExecutionAdapter extends XmlAdapter<AdaptedStepExecutions, Collection<StepExecution>> {

	@Override
	public Collection<StepExecution> unmarshal(AdaptedStepExecutions v) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdaptedStepExecutions marshal(Collection<StepExecution> v) throws Exception {
		AdaptedStepExecutions stepExecutions = new AdaptedStepExecutions(v);
		return stepExecutions;
	}

	
	
	
}