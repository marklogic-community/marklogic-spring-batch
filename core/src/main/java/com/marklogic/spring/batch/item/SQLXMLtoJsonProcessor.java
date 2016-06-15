package com.marklogic.spring.batch.item;

import java.sql.SQLXML;
import org.json.JSONObject;
import org.json.XML;

import org.springframework.batch.item.ItemProcessor;
import com.marklogic.client.helper.LoggingObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This processor converts SQLXML retrieved from relation database to json
 * using the XML class with a defined format.
 * 
 * @author viyengar
 *
 */
public class SQLXMLtoJsonProcessor extends LoggingObject
implements ItemProcessor<Map<String, Object>, Map<String, Object>> {
	public static final int PRETTY_PRINT_INDENT_FACTOR = 4;	
    @Override
    public Map<String, Object> process(Map<String, Object> columnMap) throws Exception {
        Map<String, Object> newColumnMap = new LinkedHashMap<>();
        for (String key : columnMap.keySet()) {
            JSONObject xmlJSONObj = XML.toJSONObject(((SQLXML)columnMap.get(key)).getString());
            logger.debug("Data:" + columnMap.get(key).toString());
            String jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);    
            newColumnMap.put(key, jsonPrettyPrintString);
            logger.debug(key);
            logger.debug(jsonPrettyPrintString);
        }
        return newColumnMap;
    }
}
