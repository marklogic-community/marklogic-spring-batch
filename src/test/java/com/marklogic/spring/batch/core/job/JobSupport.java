package com.marklogic.spring.batch.core.job;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.step.NoSuchStepException;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.ClassUtils;

public class JobSupport implements BeanNameAware, Job, StepLocator {
	private Map<String, Step> steps = new HashMap<>();

	private String name;

	private boolean restartable = false;

	private int startLimit = Integer.MAX_VALUE;

	private DefaultJobParametersValidator jobParametersValidator = new DefaultJobParametersValidator();

	/**
	 * Default constructor.
	 */
	public JobSupport() {
		super();
	}

	/**
	 * Convenience constructor to immediately add name (which is mandatory but
	 * not final).
	 *
	 * @param name
	 */
	public JobSupport(String name) {
		super();
		this.name = name;
	}

	/**
	 * Set the name property if it is not already set. Because of the order of
	 * the callbacks in a Spring container the name property will be set first
	 * if it is present. Care is needed with bean definition inheritance - if a
	 * parent bean has a name, then its children need an explicit name as well,
	 * otherwise they will not be unique.
	 *
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	@Override
	public void setBeanName(String name) {
		if (this.name == null) {
			this.name = name;
		}
	}

	/**
	 * Set the name property. Always overrides the default value if this object
	 * is a Spring bean.
	 *
	 * @see #setBeanName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.core.domain.IJob#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param jobParametersValidator the jobParametersValidator to set
	 */
	public void setJobParametersValidator(DefaultJobParametersValidator jobParametersValidator) {
		this.jobParametersValidator = jobParametersValidator;
	}

	public void setSteps(List<Step> steps) {
		this.steps.clear();
		for (Step step : steps) {
			this.steps.put(step.getName(), step);
		}
	}

	public void addStep(Step step) {
		this.steps.put(step.getName(), step);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.core.domain.IJob#getStartLimit()
	 */
	public int getStartLimit() {
		return startLimit;
	}

	public void setStartLimit(int startLimit) {
		this.startLimit = startLimit;
	}

	public void setRestartable(boolean restartable) {
		this.restartable = restartable;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.core.domain.IJob#isRestartable()
	 */
	@Override
	public boolean isRestartable() {
		return restartable;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.springframework.batch.core.domain.Job#run(org.springframework.batch
	 * .core.domain.JobExecution)
	 */
	@Override
	public void execute(JobExecution execution) throws UnexpectedJobExecutionException {
		throw new UnsupportedOperationException(
				"JobSupport does not provide an implementation of execute().  Use a smarter subclass.");
	}

	@Override
	public String toString() {
		return ClassUtils.getShortName(getClass()) + ": [name=" + name + "]";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.batch.core.Job#getJobParametersIncrementer()
	 */
	@Override
	public JobParametersIncrementer getJobParametersIncrementer() {
		return null;
	}

	@Override
	public JobParametersValidator getJobParametersValidator() {
		return jobParametersValidator;
	}

	@Override
	public Collection<String> getStepNames() {
		return steps.keySet();
	}

	@Override
	public Step getStep(String stepName) throws NoSuchStepException {
		final Step step = steps.get(stepName);
		if (step == null) {
			throw new NoSuchStepException("Step ["+stepName+"] does not exist for job with name ["+getName()+"]");
		}
		return step;
	}
}
