package com.marklogic.spring.batch.bind;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;


@XmlRootElement(name="jobParameters")
public class XmlJobParameters {

    private List<Parameter> listOfParameters;
    
    public XmlJobParameters() {
    	
    }
    
    public XmlJobParameters(JobParameters jobParams) {
    	listOfParameters = new ArrayList<Parameter>();
    	for (Map.Entry<String, JobParameter> entry : jobParams.getParameters().entrySet()) {
    		Parameter param = new Parameter();
    		param.key = entry.getKey();
    		JobParameter jobParam = entry.getValue();
    		param.type = jobParam.getType().toString();
    		param.identifier = Boolean.toString(jobParam.isIdentifying());
    		switch (jobParam.getType()) {
    			case STRING:
    				param.value = jobParams.getString(entry.getKey());    				
    				break;
    			case DATE:
    				param.value = jobParams.getDate(entry.getKey()).toString();
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
    	
    }
    
    @XmlElement(name="jobParameter")
    public List<Parameter> getJobParameters() {
        return listOfParameters;
    }
	
    @XmlType(name="property") static class Parameter {
        @XmlAttribute public String key;
        @XmlAttribute public String type;
        @XmlAttribute public String identifier;
        @XmlValue public String value;
    }
}
