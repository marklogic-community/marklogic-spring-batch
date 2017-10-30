package com.marklogic.spring.batch.bind;

import com.marklogic.spring.batch.core.AdaptedJobInstance;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JobInstanceAdapter extends XmlAdapter<AdaptedJobInstance, JobInstance> {

    private JobParameters jobParameters;

    public JobInstanceAdapter() {

    }

    public JobInstanceAdapter(JobParameters jobParameters) {
        this.jobParameters = jobParameters;
    }

    @Override
    public JobInstance unmarshal(AdaptedJobInstance v) throws Exception {
        JobInstance jobInstance = new JobInstance(v.getId(), v.getJobName());
        jobInstance.setVersion(v.getVersion());
        return jobInstance;
    }

    @Override
    public AdaptedJobInstance marshal(JobInstance v) throws Exception {
        AdaptedJobInstance aji = new AdaptedJobInstance(v);
        if (!(jobParameters == null)) {
            aji.setJobKey(jobParameters);
        }
        return aji;
    }

}
