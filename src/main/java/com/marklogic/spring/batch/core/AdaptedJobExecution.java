package com.marklogic.spring.batch.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.batch.runtime.BatchStatus;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.marklogic.client.pojo.annotation.Id;
import com.marklogic.spring.batch.bind.ExecutionContextAdapter;
import com.marklogic.spring.batch.bind.JobInstanceAdapter;
import com.marklogic.spring.batch.bind.JobParametersAdapter;
import com.marklogic.spring.batch.bind.StepExecutionAdapter;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

@XmlRootElement(name = "jobExecution")
public class AdaptedJobExecution {
	
	private JobParameters jobParameters;
	private List<StepExecution> stepExecution;
	private JobInstance jobInstance;
	private Long jobInstanceId;
	private Date createDateTime;
	private Date startDateTime;
	private Date endDateTime;
	private Date lastUpdatedDateTime;
	private String status = BatchStatus.STARTING.toString();
	private String exitStatus;
	private Long id;
	private String uri;
	private Integer version;
	private ExecutionContext executionContext;

	protected AdaptedJobExecution() { 
		stepExecution = new ArrayList<StepExecution>();
	}
	
	public AdaptedJobExecution(JobExecution jobExecution) {
		this();
		setId(jobExecution.getId());
		if (jobExecution.getVersion() == null) {
			jobExecution.setVersion(new Integer(0));
		}
		this.version = jobExecution.getVersion();
		this.jobInstance = jobExecution.getJobInstance();
		this.jobParameters = jobExecution.getJobParameters();
		this.createDateTime = jobExecution.getCreateTime();
		this.endDateTime = jobExecution.getEndTime();
		this.lastUpdatedDateTime = jobExecution.getLastUpdated();
		this.startDateTime = jobExecution.getStartTime();
		if (jobExecution.getStatus() == null) {
			this.status = BatchStatus.STARTING.toString();
		} else {
			this.status = jobExecution.getStatus().toString();
		}
		this.exitStatus = jobExecution.getExitStatus().toString();
		this.stepExecution = new ArrayList<StepExecution>(jobExecution.getStepExecutions());
		this.executionContext = jobExecution.getExecutionContext();
	}

		
	@XmlJavaTypeAdapter(StepExecutionAdapter.class)
	@XmlElementWrapper( name="stepExecutions", namespace=MarkLogicSpringBatch.STEP_EXECUTION_NAMESPACE )
	@XmlElement(name = "stepExecution", namespace=MarkLogicSpringBatch.STEP_EXECUTION_NAMESPACE)
	public List<StepExecution> getStepExecutions() {
		return stepExecution;
	}

	public void setStepExecutions(List<StepExecution> stepExecutions) {
		this.stepExecution = stepExecutions;
	}	

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
		setUri(MarkLogicSpringBatch.SPRING_BATCH_DIR + id + ".xml");
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
	
	public Date getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(Date createDateTime) {
		this.createDateTime = createDateTime;
	}

	@XmlJavaTypeAdapter(JobInstanceAdapter.class)
	@XmlElement(namespace=MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE)
	public JobInstance getJobInstance() {
		return jobInstance;
	}

	public void setJobInstance(JobInstance jobInstance) {
		this.jobInstance = jobInstance;
	}
	
	@XmlJavaTypeAdapter(JobParametersAdapter.class)
	@XmlElement(namespace=MarkLogicSpringBatch.JOB_PARAMETER_NAMESPACE)
	public JobParameters getJobParameters() {
		return jobParameters;
	}
	
	public void setJobParameters(JobParameters jobParameters) {
		this.jobParameters = jobParameters;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(String exitStatus) {
		this.exitStatus = exitStatus;
	}

	public String getExitCode() {
		return exitStatus.split("=|;")[1];
	}

	public void setExitCode(String exitCode) {
		return;
	}

	public Long getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}

	@XmlJavaTypeAdapter(ExecutionContextAdapter.class)
	@XmlElement(namespace=MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE)
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
	
}
