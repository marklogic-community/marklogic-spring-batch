package com.marklogic.spring.batch.bind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.StepExecution;

import com.marklogic.spring.batch.core.AdaptedStepExecutions;
import com.marklogic.spring.batch.core.AdaptedStepExecutions.AdaptedStepExecution;

public class StepExecutionAdapter extends XmlAdapter<AdaptedStepExecutions, Collection<StepExecution>> {

	@Override
	public Collection<StepExecution> unmarshal(AdaptedStepExecutions v) throws Exception {
		Collection<StepExecution> steps = new CopyOnWriteArraySet<StepExecution>();
		for (AdaptedStepExecution adaptedStep : new ArrayList<AdaptedStepExecution>(v.getStepExecutions())) {
			StepExecution step = new StepExecution(adaptedStep.stepName, v.getJobExecution());
			steps.add(step);
		}
		return steps;
	}

	@Override
	public AdaptedStepExecutions marshal(Collection<StepExecution> v) throws Exception {
		AdaptedStepExecutions adaptedSteps = new AdaptedStepExecutions();
		return adaptedSteps;
	}

	
	
	
}