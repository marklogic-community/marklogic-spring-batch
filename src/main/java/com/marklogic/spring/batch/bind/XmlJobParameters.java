package com.marklogic.spring.batch.bind;

import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


@XmlRootElement(name="jobParameters")
public class XmlJobParameters {

    private Properties props = new Properties();
    
    public XmlJobParameters() {
    	
    }
    
    public XmlJobParameters(Properties props) {
    	this.props = props;
    }
    
    @XmlElement(name="jobParameter")
    public XmlProperties getProperties() {
        return new XmlProperties(props);
    }
	
    @XmlType(name="property") static class XmlProperty {
        @XmlAttribute public String key;
        @XmlValue public String value;
    }
 
    static class XmlProperties extends AbstractCollection<XmlProperty> {
        private final Properties props;
        public XmlProperties(Properties props) { this.props = props; }
        public int size() { return props.size(); }
        public Iterator<XmlProperty> iterator() {
            return new XmlPropertyIterator(props.entrySet().iterator());
        }
        public boolean add(XmlProperty xml) {
            return !xml.value.equals(props.setProperty(xml.key, xml.value));
        }
    }
 
    static class XmlPropertyIterator implements Iterator<XmlProperty> {
        private final Iterator<Map.Entry<Object, Object>> base;
        
        public XmlPropertyIterator(Iterator<Map.Entry<Object, Object>> base) {
            this.base = base;
        }
        public boolean hasNext() { 
        	return base.hasNext(); 
        }
        
        public void remove() { base.remove(); }
        
        public XmlProperty next() {	
            Map.Entry<?, ?> entry = base.next();
            XmlProperty xml = new XmlProperty();
            xml.key = entry.getKey().toString();
            xml.value = entry.getValue().toString();
            return xml;
        }
    }

}
