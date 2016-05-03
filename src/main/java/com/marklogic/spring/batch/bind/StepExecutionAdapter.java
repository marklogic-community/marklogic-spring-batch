package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import com.marklogic.spring.batch.core.AdaptedStepExecution;

public class StepExecutionAdapter extends XmlAdapter<AdaptedStepExecution, StepExecution> {

	@Override
	public StepExecution unmarshal(AdaptedStepExecution v) throws Exception {
		StepExecution step = new StepExecution(v.getStepName(), new JobExecution(v.getJobExecutionId()));
		step.setId(v.getId());
		step.setStartTime(v.getStartTime());
		step.setReadSkipCount(v.getReadSkipCount());
		step.setWriteSkipCount(v.getWriteSkipCount());
		step.setProcessSkipCount(v.getProcessSkipCount());
		step.setReadCount(v.getReadCount());
		step.setWriteCount(v.getWriteCount());
		step.setFilterCount(v.getFilterCount());
		step.setRollbackCount(v.getRollbackCount());
		step.setExitStatus(new ExitStatus(v.getExitCode()));
		step.setLastUpdated(v.getLastUpdated());
		return step;
	}

	@Override
	public AdaptedStepExecution marshal(StepExecution v) throws Exception {
		return new AdaptedStepExecution(v);
	}	
	
}