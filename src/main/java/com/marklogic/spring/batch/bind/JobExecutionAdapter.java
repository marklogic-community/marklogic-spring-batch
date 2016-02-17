package com.marklogic.spring.batch.bind;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;

import com.marklogic.spring.batch.core.AdaptedJobExecution;

public class JobExecutionAdapter extends XmlAdapter<AdaptedJobExecution, JobExecution>{

	@Override
	public JobExecution unmarshal(AdaptedJobExecution v) throws Exception {
		JobExecution jobExec = new JobExecution(v.getId(), v.getJobParameters());
		jobExec.setCreateTime(v.getCreateDateTime());
		jobExec.setEndTime(v.getEndDateTime());
		jobExec.setLastUpdated(v.getLastUpdatedDateTime());
		jobExec.setStartTime(v.getStartDateTime());
		jobExec.setJobInstance(v.getJobInstance());
		/*
		List<StepExecution> listOfSteps = new ArrayList<StepExecution>();
		while (v.getStepExecutions().iterator().hasNext()) {
			listOfSteps.add(v.getStepExecutions().iterator().next());
		}
		jobExec.addStepExecutions(listOfSteps);
		*/
		jobExec.setStatus(BatchStatus.valueOf(v.getStatus()));
		//jobExec.setExitStatus();
		
		return jobExec;
	}

	@Override
	public AdaptedJobExecution marshal(JobExecution v) throws Exception {
		return new AdaptedJobExecution(v);
	}

}
