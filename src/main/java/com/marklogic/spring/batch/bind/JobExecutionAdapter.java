package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;

import com.marklogic.spring.batch.core.AdaptedJobExecution;

public class JobExecutionAdapter extends XmlAdapter<AdaptedJobExecution, JobExecution>{

	@Override
	public JobExecution unmarshal(AdaptedJobExecution v) throws Exception {
		JobExecution jobExec = new JobExecution(v.getId(), v.getJobParameters());
		jobExec.setJobInstance(v.getJobInstance());
		jobExec.setCreateTime(v.getCreateDateTime());
		jobExec.setEndTime(v.getEndDateTime());
		jobExec.setLastUpdated(v.getLastUpdatedDateTime());
		jobExec.setStartTime(v.getStartDateTime());
		jobExec.setStatus(BatchStatus.valueOf(v.getStatus()));
		jobExec.setExitStatus(new ExitStatus(v.getExitCode(), ""));
		jobExec.addStepExecutions(v.getStepExecutions());
		jobExec.setVersion(v.getVersion());
		jobExec.setExecutionContext(v.getExecutionContext());
		return jobExec;
	}

	@Override
	public AdaptedJobExecution marshal(JobExecution v) throws Exception {
		return new AdaptedJobExecution(v);
	}

}
