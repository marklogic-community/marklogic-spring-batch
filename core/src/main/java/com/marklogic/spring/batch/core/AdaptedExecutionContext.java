package com.marklogic.spring.batch.core;

import org.springframework.batch.item.ExecutionContext;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@XmlRootElement(name = "executionContext", namespace = MarkLogicSpringBatch.JOB_NAMESPACE)
@XmlType(namespace = MarkLogicSpringBatch.JOB_NAMESPACE)
public class AdaptedExecutionContext {

    private Map<String, Object> map = new HashMap<>();
    private int hashCode;
    private boolean dirtyFlag;

    public AdaptedExecutionContext() {

    }

    public AdaptedExecutionContext(ExecutionContext exeContext) throws InstantiationException, IllegalAccessException {
        this.hashCode = exeContext.hashCode();
        this.dirtyFlag = exeContext.isDirty();

        // Get a set of the entries
        Set<Entry<String, Object>> set = exeContext.entrySet();
        // Get an iterator
        Iterator<Entry<String, Object>> i = set.iterator();
        // Display elements
        while (i.hasNext()) {
            Entry<String, Object> me = i.next();
            String name = me.getKey();
            Object obj = getValue(me.getValue());
            map.put(name, obj);
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

    private Object getValue(Object value) {

        if (String.class.isInstance(value)) {
            value = value;
        } else if (Long.class.isInstance(value)) {
            value = value;
        } else if (Double.class.isInstance(value)) {
            value = value;
        } else if (Integer.class.isInstance(value)) {
            value = value;
        } else if (Boolean.class.isInstance(value)) {
            value = value;
        } else {
            value = value;
        }
        return value;
    }

}
