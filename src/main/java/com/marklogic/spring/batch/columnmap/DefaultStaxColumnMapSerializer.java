package com.marklogic.spring.batch.columnmap;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.marklogic.client.helper.LoggingObject;

/**
 * Default implementation that uses the Stax library to create an XML structure. Any XML library, or even a templating
 * engine like Velocity/Freemarker, could be used here instead.
 */
public class DefaultStaxColumnMapSerializer extends LoggingObject implements ColumnMapSerializer {

    private XMLOutputFactory xmlOutputFactory;

    public DefaultStaxColumnMapSerializer() {
        this.xmlOutputFactory = XMLOutputFactory.newFactory();
    }

    @Override
    public String serializeColumnMap(Map<String, Object> columnMap, String rootLocalName, String rootNamespaceUri) {
        StringWriter out = new StringWriter();
        try {
            XMLStreamWriter sw = xmlOutputFactory.createXMLStreamWriter(out);
            sw.writeStartElement(rootLocalName);
            writeColumnMap(columnMap, sw);
            sw.writeEndElement();
            sw.flush();
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        return out.toString();
    }

    /**
     * Recursive function that will call itself when it finds that the value of a key/value pair in the column map is a
     * Map<String, Object> itself.
     * 
     * @param columnMap
     * @param sw
     * @throws XMLStreamException
     */
    private void writeColumnMap(Map<String, Object> columnMap, XMLStreamWriter sw) throws XMLStreamException {
        for (String key : columnMap.keySet()) {
            Object value = columnMap.get(key);
            if (value instanceof List) {
                List list = (List) value;
                for (Object item : list) {
                    sw.writeStartElement(key);
                    if (item instanceof Map) {
                        writeColumnMap((Map<String, Object>) item, sw);
                    } else {
                        sw.writeCharacters(item.toString());
                    }
                    sw.writeEndElement();
                }
            } else if (value instanceof Map) {
                sw.writeStartElement(key);
                writeColumnMap((Map<String, Object>) value, sw);
                sw.writeEndElement();
            } else {
                sw.writeStartElement(key);
                sw.writeCharacters(columnMap.get(key).toString());
                sw.writeEndElement();
            }
        }
    }

}
