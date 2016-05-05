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
	
	private Map<String, Object> map = new HashMap<String, Object>();
	
	private int hashCode;
	private boolean dirtyFlag;
	
	public AdaptedExecutionContext() {
		
	}
	
	public AdaptedExecutionContext(ExecutionContext exeContext) {
		this.hashCode = exeContext.hashCode();
		this.dirtyFlag = exeContext.isDirty();
		
		// Get a set of the entries
		Set<Entry<String, Object>> set = exeContext.entrySet();
	    // Get an iterator
	    Iterator<Entry<String,Object>> i = set.iterator();
	      // Display elements
	    while(i.hasNext()) {
	    	Entry<String, Object> me = i.next();
	    	String name = me.getKey();
	    	Object value = me.getValue();
	    	map.put(name, value);
	    }
	}
	
	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}
	
	public int getHashCode() {
		return hashCode;
	}

	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}

	public boolean isDirtyFlag() {
		return dirtyFlag;
	}

	public void setDirtyFlag(boolean dirtyFlag) {
		this.dirtyFlag = dirtyFlag;
	}

}
