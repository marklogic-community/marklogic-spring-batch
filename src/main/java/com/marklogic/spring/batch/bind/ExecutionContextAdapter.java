package com.marklogic.spring.batch.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.springframework.batch.item.ExecutionContext;

import com.marklogic.spring.batch.core.AdaptedExecutionContext;

public class ExecutionContextAdapter extends XmlAdapter<AdaptedExecutionContext, ExecutionContext> {

	@Override
	public ExecutionContext unmarshal(AdaptedExecutionContext v) throws Exception {
		return new ExecutionContext(v.getMap()); 
	}

	@Override
	public AdaptedExecutionContext marshal(ExecutionContext v) throws Exception {
		return new AdaptedExecutionContext(v);
	}

}
