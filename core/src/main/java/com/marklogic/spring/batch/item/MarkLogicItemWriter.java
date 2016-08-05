package com.marklogic.spring.batch.item;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.document.DocumentWriteSet;
import com.marklogic.client.document.GenericDocumentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/*
The MarkLogicItemWriter is an ItemWriter used to write any type of document to MarkLogic.  It expects a
<a href="http://docs.marklogic.com/javadoc/client/com/marklogic/client/document/DocumentWriteOperation.html">DocumentWriteOperation</a> class.

@see <a href="">MarkLogicWriteHandle</a>
 */
public class MarkLogicItemWriter implements ItemWriter<DocumentWriteOperation> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected GenericDocumentManager docMgr;
    private DatabaseClient client;
    private String outputUriPrefix;
    private String outputUriReplace;
    private String outputUriSuffix;
    
    public MarkLogicItemWriter(DatabaseClient client) {
        this.client = client;
        docMgr = client.newDocumentManager();
    }
    
    @Override
    public void write(List<? extends DocumentWriteOperation> items) throws Exception {
        DocumentWriteSet batch = docMgr.newWriteSet();
        for (DocumentWriteOperation item : items) {
            String uri = item.getUri();
            uri = (outputUriReplace != null) ? applyOutputUriReplace(uri, outputUriReplace) : uri;
            uri = (outputUriPrefix != null) ? outputUriPrefix + uri : uri;
            uri = (outputUriSuffix != null) ? uri + outputUriSuffix : uri;
            batch.add(uri, item.getMetadata(), item.getContent());
        }
        docMgr.write(batch);
    }
    
    private String applyOutputUriReplace(String uri, String outputUriReplace) {
        String[] regexReplace = outputUriReplace.split(",");
        for (int i = 0; i < regexReplace.length; i=i+2) {
            String regex = regexReplace[i];
            String replace = regexReplace[i+1];
            uri = uri.replaceAll(regex, replace);
        }
        return uri;
    }
    
    /*
    Prepends a string to the URI after substitution
     */
    public void setOutputUriPrefix(String outputUriPrefix) {
        this.outputUriPrefix = outputUriPrefix;
    }
    
    /*
    The outputUriReplace option accepts a comma delimited list of regular expression and replacement string pairs
    @param outputUriReplace Example regex,'replaceString',regex,'replaceString'
     */
    public void setOutputUriReplace(String outputUriReplace) {
        this.outputUriReplace = outputUriReplace;
    }
    
    /*
    Appends a string to the URI after substitution
     */
    public void setOutputUriSuffix(String outputUriSuffix) {
        this.outputUriSuffix = outputUriSuffix;
    }
    
    
}
