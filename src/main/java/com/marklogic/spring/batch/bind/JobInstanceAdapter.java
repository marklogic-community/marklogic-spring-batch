package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.JobInstance;

import com.marklogic.spring.batch.core.AdaptedJobInstance;

public class JobInstanceAdapter extends XmlAdapter<AdaptedJobInstance, JobInstance> {

	@Override
	public JobInstance unmarshal(AdaptedJobInstance v) throws Exception {
		JobInstance jobInstance = new JobInstance(v.getId(), v.getJobName());
		jobInstance.setVersion(v.getVersion());
		return jobInstance;
	}

	@Override
	public AdaptedJobInstance marshal(JobInstance v) throws Exception {
		AdaptedJobInstance adaptedJobInstance = new AdaptedJobInstance(v.getId(), v.getJobName());
		adaptedJobInstance.setVersion(v.getVersion());
		return adaptedJobInstance;
	}

}
