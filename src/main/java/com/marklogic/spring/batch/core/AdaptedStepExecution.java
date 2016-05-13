package com.marklogic.spring.batch.core;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;

import com.marklogic.spring.batch.bind.ExecutionContextAdapter;

@XmlRootElement(name = "stepExecution", namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
public class AdaptedStepExecution {
	
	private Long id;
	private Integer version = new Integer(0);
	private Long jobExecutionId;
	private Long jobInstanceId;
	private String jobName;
	private String stepName;
	private BatchStatus status = BatchStatus.STARTING;
	private int readCount = 0;
	private int writeCount = 0;
	private int commitCount = 0;
	private int rollbackCount = 0;
	private int readSkipCount = 0;
	private int processSkipCount = 0;
	private int writeSkipCount = 0;
	private Date startTime = null;
	private Date endTime = null;
	private Date lastUpdated = null;
	private ExecutionContext executionContext;
	private String exitStatus = ExitStatus.EXECUTING.toString();
	private boolean terminateOnly;
	private int filterCount;
	private List<Throwable> failureExceptions = new CopyOnWriteArrayList<Throwable>();
	
	public AdaptedStepExecution() { 
		
	}
	
	public AdaptedStepExecution(StepExecution stepExec) {
		this.setId(stepExec.getId());
		this.setJobInstanceId(stepExec.getJobExecution().getJobInstance().getId());
		this.setStepName(stepExec.getStepName());
		this.setStatus(stepExec.getStatus());
		this.setReadSkipCount(stepExec.getReadSkipCount());
		this.setWriteSkipCount(stepExec.getWriteSkipCount());
		this.setProcessSkipCount(stepExec.getProcessSkipCount());
		this.setRollbackCount(stepExec.getRollbackCount());
		this.setJobExecutionId(stepExec.getJobExecutionId());
		this.setReadCount(stepExec.getReadCount());
		this.setWriteCount(stepExec.getWriteCount());
		this.setFilterCount(stepExec.getFilterCount());
		this.setVersion(stepExec.getVersion());
		this.setExitStatus(stepExec.getExitStatus());
		this.setVersion(stepExec.getVersion());
		this.setJobName(stepExec.getJobExecution().getJobInstance().getJobName());
		this.setStartTime(stepExec.getStartTime());
		this.setEndTime(stepExec.getEndTime());
		this.setLastUpdated(stepExec.getLastUpdated());	
		this.setExecutionContext(stepExec.getExecutionContext());
	}
	
	public String getExitCode() {
		return exitStatus.split("=|;")[1];
	}
	
	public JobExecution getJobExecution() {
		//return jobExecution;
		return null;
	}

	public void setJobExecution(JobExecution jobExecution) {
		//this.jobExecution = jobExecution;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public BatchStatus getStatus() {
		return status;
	}

	public void setStatus(BatchStatus status) {
		this.status = status;
	}

	public int getReadCount() {
		return readCount;
	}

	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}

	public int getWriteCount() {
		return writeCount;
	}

	public void setWriteCount(int writeCount) {
		this.writeCount = writeCount;
	}

	public int getCommitCount() {
		return commitCount;
	}

	public void setCommitCount(int commitCount) {
		this.commitCount = commitCount;
	}

	public int getRollbackCount() {
		return rollbackCount;
	}

	public void setRollbackCount(int rollbackCount) {
		this.rollbackCount = rollbackCount;
	}

	public int getReadSkipCount() {
		return readSkipCount;
	}

	public void setReadSkipCount(int readSkipCount) {
		this.readSkipCount = readSkipCount;
	}

	public int getProcessSkipCount() {
		return processSkipCount;
	}

	public void setProcessSkipCount(int processSkipCount) {
		this.processSkipCount = processSkipCount;
	}

	public int getWriteSkipCount() {
		return writeSkipCount;
	}

	public void setWriteSkipCount(int writeSkipCount) {
		this.writeSkipCount = writeSkipCount;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getExitStatus() {
		return exitStatus;
	}

	public void setExitStatus(ExitStatus exitStatus) {
		this.exitStatus = exitStatus.toString();
	}

	public boolean isTerminateOnly() {
		return terminateOnly;
	}

	public void setTerminateOnly(boolean terminateOnly) {
		this.terminateOnly = terminateOnly;
	}

	public int getFilterCount() {
		return filterCount;
	}

	public void setFilterCount(int filterCount) {
		this.filterCount = filterCount;
	}

	public List<Throwable> getFailureExceptions() {
		return failureExceptions;
	}

	public void setFailureExceptions(List<Throwable> failureExceptions) {
		this.failureExceptions = failureExceptions;
	}

	public Long getJobExecutionId() {
		return jobExecutionId;
	}

	public void setJobExecutionId(Long jobExecutionId) {
		this.jobExecutionId = jobExecutionId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	@XmlJavaTypeAdapter(ExecutionContextAdapter.class)
	@XmlElement(namespace=MarkLogicSpringBatch.JOB_NAMESPACE)
	public ExecutionContext getExecutionContext() {
		return executionContext;
	}

	public void setExecutionContext(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Long getJobInstanceId() {
		return jobInstanceId;
	}

	public void setJobInstanceId(Long jobInstanceId) {
		this.jobInstanceId = jobInstanceId;
	}    
    	
}
