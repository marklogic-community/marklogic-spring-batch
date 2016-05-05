package com.marklogic.spring.batch.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.springframework.batch.item.ExecutionContext;

@XmlRootElement(name = "executionContext", namespace=MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE)
@XmlType(namespace=MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE)
public class AdaptedExecutionContext {
	
	private Map<String, String> map = new HashMap<String, String>();
	
	private int hashCode;
	
	public AdaptedExecutionContext() {
		
	}
	
	public AdaptedExecutionContext(ExecutionContext exeContext, int hashCode) {
		this.hashCode = hashCode;
		// Get a set of the entries
		Set<Entry<String, Object>> set = exeContext.entrySet();
	    // Get an iterator
	    Iterator<Entry<String,Object>> i = set.iterator();
	      // Display elements
	    while(i.hasNext()) {
	    	Entry<String, Object> me = i.next();
	    	String name = me.getKey();
	    	String value = me.getValue().toString();
	    	map.put(name, value);
	    }
	}
	
	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}

	@XmlTransient
	public Map<String, Object> getExecutionContextMap() {
		Map<String, Object> ecStuff = new HashMap<String, Object>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			ecStuff.put(entry.getKey(), entry.getValue());
		}
		return ecStuff;
	}
	
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

}
