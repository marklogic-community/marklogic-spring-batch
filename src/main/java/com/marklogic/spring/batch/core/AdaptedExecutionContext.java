package com.marklogic.spring.batch.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.batch.item.ExecutionContext;

@XmlRootElement(name = "executionContext", namespace=MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE)
public class AdaptedExecutionContext {
	
	private Map<String, String> valuesMap = new HashMap<String, String>();
	
	public AdaptedExecutionContext() {
		
	}
	
	public AdaptedExecutionContext(ExecutionContext exeContext) {
		// Get a set of the entries
		Set<Entry<String, Object>> set = exeContext.entrySet();
	    // Get an iterator
	    Iterator<Entry<String,Object>> i = set.iterator();
	      // Display elements
	    while(i.hasNext()) {
	    	Entry<String, Object> me = i.next();
	    	String name = me.getKey();
	    	String value = me.getValue().toString();
	    	valuesMap.put(name, value);
	    }
	}

}
