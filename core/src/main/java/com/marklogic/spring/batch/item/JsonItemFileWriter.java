package com.marklogic.spring.batch.item;

import org.springframework.batch.item.ItemWriter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 * This writer writes the json file that has been extracted and processed from SQLXML to a defined location
 * on the file system. The path of the file location is specified as part of the job configuration. The writer 
 * constructs the filename based on the column name of the extracted xml from RDBMS.
 * 
 * @author viyengar
 *
 */
public class JsonItemFileWriter extends AbstractDocumentWriter implements ItemWriter<Map<String, Object>> {

    // Configurable
    private String rootElementName;
    private String outputFilePath;
    public JsonItemFileWriter(String rootElementName, String outputFilePath) {
        this.rootElementName = rootElementName;
        this.outputFilePath = outputFilePath;
    }    	
    @Override
    public void write(List<? extends Map<String, Object>> items) throws Exception {
        logger.debug("Calling write to write json files....");
        int fileIndex = 1;
        String fileName = "";
        for (Map<String, Object> columnMap : items) {
            String idKey = columnMap.keySet().iterator().next();
            String jsonString = (String)columnMap.get(idKey);
            logger.debug("From Writer:" + jsonString);
            fileName = rootElementName + "-" + idKey + fileIndex + ".json";
            writeFile(fileName, jsonString);
            fileIndex++;
        }                
    }
	public void writeFile(String fileName, String jsonString) {
		FileOutputStream fileOutputStream = null;
		File file = new File(outputFilePath + fileName);
		try 
		{
			fileOutputStream = new FileOutputStream(file);
			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = jsonString.getBytes();

			fileOutputStream.write(contentInBytes);
			fileOutputStream.flush();
			logger.info("Wrote File [" + outputFilePath + fileName + "]");
		} 
		catch (IOException ioe) 
		{
			logger.info("Error writing file:" + ioe.getMessage());
			ioe.printStackTrace();
		}
		finally 
		{
			try
			{
    			if (fileOutputStream != null)
    			{
    				fileOutputStream.close();
    			}
			}
			catch (IOException ioe)
			{
				logger.info("Error closing FileOutputStream:" + ioe.getMessage());
    			ioe.printStackTrace();    				
			}
		}
	}     
}
