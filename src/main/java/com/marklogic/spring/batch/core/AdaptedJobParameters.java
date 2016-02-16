package com.marklogic.spring.batch.core;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name="jobParameters")
public class AdaptedJobParameters {

	private List<AdaptedJobParameter> parameters;
	
	@XmlElement(name="jobParameter")
    public List<AdaptedJobParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<AdaptedJobParameter> parameters) {
		this.parameters = parameters;
	}
    
    public AdaptedJobParameters() {
    	
    }
	
    public static class AdaptedJobParameter {
        @XmlAttribute 
        public String key;
        
        @XmlAttribute 
        public String type;
        
        @XmlAttribute 
        public boolean identifier;
        
        @XmlValue 
        public String value;
    }
}
