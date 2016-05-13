package com.marklogic.spring.batch.bind;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

import com.marklogic.spring.batch.core.AdaptedJobParameters;

public class JobParametersAdapter extends XmlAdapter<AdaptedJobParameters, JobParameters> {
	
	@Override
	public JobParameters unmarshal(AdaptedJobParameters params) throws Exception {
		JobParametersBuilder jobParametersBuilder  = new JobParametersBuilder();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		for (AdaptedJobParameters.AdaptedJobParameter param : params.getParameters()) {
    		switch (param.type) {
    			case "STRING":
    				jobParametersBuilder.addString(param.key, param.value, param.identifier);
    				break;
    			case "DATE":
    				jobParametersBuilder.addDate(param.key, df.parse(param.value), param.identifier);
    				break;
    			case "DOUBLE":
    				jobParametersBuilder.addDouble(param.key, Double.valueOf(param.value), param.identifier);
    				break;
    			case "LONG":
    				jobParametersBuilder.addLong(param.key, Long.valueOf(param.value), param.identifier);
    				break;
    		}
		}
		return jobParametersBuilder.toJobParameters();
	}

	@Override
	public AdaptedJobParameters marshal(JobParameters jobParams) throws Exception {
		AdaptedJobParameters adaptedJobParams = new AdaptedJobParameters();
    	List<AdaptedJobParameters.AdaptedJobParameter> listOfParameters = new ArrayList<AdaptedJobParameters.AdaptedJobParameter>();
    	if (jobParams == null) {
    		return adaptedJobParams;
    	} else {		
	    	for (Map.Entry<String, JobParameter> entry : jobParams.getParameters().entrySet()) {
	    		AdaptedJobParameters.AdaptedJobParameter param = new AdaptedJobParameters.AdaptedJobParameter();
	    		param.key = entry.getKey();
	    		JobParameter jobParam = entry.getValue();
	    		param.type = jobParam.getType().toString();
	    		param.identifier = jobParam.isIdentifying();
	    		switch (jobParam.getType()) {
	    			case STRING:
	    				param.value = jobParams.getString(entry.getKey());    				
	    				break;
	    			case DATE:
	    				String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(jobParams.getDate(entry.getKey()));
	    				param.value = formatted;
	    				break;
	    			case DOUBLE:
	    				param.value = jobParams.getDouble(entry.getKey()).toString();
	    				break;
	    			case LONG:
	    				param.value = jobParams.getLong(entry.getKey()).toString();
	    				break;
	    		}
	    		listOfParameters.add(param);
	    	}
	    	adaptedJobParams.setParameters(listOfParameters);
    	}
		return adaptedJobParams;
	}

}
