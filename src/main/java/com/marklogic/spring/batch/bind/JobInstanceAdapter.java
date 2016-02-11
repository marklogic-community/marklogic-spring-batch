package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.JobInstance;

public class JobInstanceAdapter extends XmlAdapter<AdaptedJobInstance, JobInstance> {

	@Override
	public JobInstance unmarshal(AdaptedJobInstance v) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdaptedJobInstance marshal(JobInstance v) throws Exception {
		AdaptedJobInstance adaptedJobInstance = new AdaptedJobInstance(v.getId(), v.getJobName());
		return adaptedJobInstance;
	}

}
