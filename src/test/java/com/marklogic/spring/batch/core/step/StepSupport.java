package com.marklogic.spring.batch.core.step;

import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.beans.factory.BeanNameAware;

public class StepSupport implements Step, BeanNameAware {

	private String name;

	private int startLimit = Integer.MAX_VALUE;

	private boolean allowStartIfComplete;

	/**
	 * Default constructor for {@link StepSupport}.
	 */
	public StepSupport() {
		super();
	}

	/**
	 * @param string
	 */
	public StepSupport(String string) {
		super();
		this.name = string;
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Set the name property if it is not already set. Because of the order of the callbacks in a Spring container the
	 * name property will be set first if it is present. Care is needed with bean definition inheritance - if a parent
	 * bean has a name, then its children need an explicit name as well, otherwise they will not be unique.
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
	 * Set the name property. Always overrides the default value if this object is a Spring bean.
	 *
	 * @see #setBeanName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getStartLimit() {
		return this.startLimit;
	}

	/**
	 * Public setter for the startLimit.
	 *
	 * @param startLimit the startLimit to set
	 */
	public void setStartLimit(int startLimit) {
		this.startLimit = startLimit;
	}

	@Override
	public boolean isAllowStartIfComplete() {
		return this.allowStartIfComplete;
	}

	/**
	 * Public setter for the shouldAllowStartIfComplete.
	 *
	 * @param allowStartIfComplete the shouldAllowStartIfComplete to set
	 */
	public void setAllowStartIfComplete(boolean allowStartIfComplete) {
		this.allowStartIfComplete = allowStartIfComplete;
	}

	/**
	 * Not supported but provided so that tests can easily create a step.
	 *
	 * @throws UnsupportedOperationException always
	 *
	 * @see org.springframework.batch.core.Step#execute(org.springframework.batch.core.StepExecution)
	 */
	@Override
	public void execute(StepExecution stepExecution) throws JobInterruptedException, UnexpectedJobExecutionException {
		throw new UnsupportedOperationException(
				"Cannot process a StepExecution.  Use a smarter subclass of StepSupport.");
	}
}