package com.marklogic.spring.batch.core;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.spring.batch.bind.JobInstanceAdapter;
import com.marklogic.spring.batch.bind.JobParametersAdapter;
import com.marklogic.spring.batch.bind.StepExecutionAdapter;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;

@XmlRootElement(name = "jobExecution")
public class AdaptedJobExecution {
	
	private JobParameters jobParameters;
	private JobInstance jobInstance;
	private Date createDateTime;
	private Date startDateTime;
	private Date endDateTime;
	private Date lastUpdatedDateTime;
	private String status;
	private String exitCode;
	private String exitMessage;
	private Long id;
	private String uri;

	//private Collection<StepExecution> stepExecutions = new CopyOnWriteArraySet<StepExecution>();

	protected AdaptedJobExecution() { }
	
	public AdaptedJobExecution(JobExecution jobExecution) {
		setId(jobExecution.getId());
		this.jobInstance = jobExecution.getJobInstance();
		this.jobParameters = jobExecution.getJobParameters();
		this.createDateTime = jobExecution.getCreateTime();
		this.endDateTime = jobExecution.getEndTime();
		this.lastUpdatedDateTime = jobExecution.getLastUpdated();
		this.startDateTime = jobExecution.getStartTime();
		this.status = jobExecution.getStatus().toString();
		this.exitCode = jobExecution.getExitStatus().toString();
		//this.stepExecutions = jobExecution.getStepExecutions();
	}
	 /*	
	@XmlJavaTypeAdapter(StepExecutionAdapter.class)
	public Collection<StepExecution> getStepExecutions() {
		return stepExecutions;
	}

	public void setStepExecutions(Collection<StepExecution> stepExecutions) {
		this.stepExecutions = stepExecutions;
	}
	*/
	@Id
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		setUri(MarkLogicSpringBatch.SPRING_BATCH_DIR + id);
		this.id = id;
	}

	public Date getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	public Date getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	public Date getLastUpdatedDateTime() {
		return lastUpdatedDateTime;
	}

	public void setLastUpdatedDateTime(Date lastUpdatedDateTime) {
		this.lastUpdatedDateTime = lastUpdatedDateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExitCode() {
		return exitCode;
	}

	public void setExitCode(String exitCode) {
		this.exitCode = exitCode;
	}

	public String getExitMessage() {
		return exitMessage;
	}

	public void setExitMessage(String exitMessage) {
		this.exitMessage = exitMessage;
	}
	
	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	@XmlJavaTypeAdapter(JobInstanceAdapter.class)
	public JobInstance getJobInstance() {
		return jobInstance;
	}

	public void setJobInstance(JobInstance jobInstance) {
		this.jobInstance = jobInstance;
	}
	
	@XmlJavaTypeAdapter(JobParametersAdapter.class)
	public JobParameters getJobParameters() {
		return jobParameters;
	}
	
	public void setJobParameters(JobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}
	
}
