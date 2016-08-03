package com.marklogic.client.io;

import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarkLogicWriteHandle implements DocumentWriteOperation {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private DocumentUriTemplate uriTemplate;
    private String uri;
    private DocumentMetadataHandle metadataHandle;
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    private AbstractWriteHandle handle;
    private OperationType opType;
    
    public void setUriTemplate(DocumentUriTemplate uriTemplate) {
        this.uriTemplate = uriTemplate;
    }
    
    public void setMetadataHandle(DocumentMetadataHandle metadataHandle) {
        this.metadataHandle = metadataHandle;
    }
    
    public void setHandle(AbstractWriteHandle handle) {
        this.handle = handle;
    }
    
    public void setOpType(OperationType opType) {
        this.opType = opType;
    }
    
    @Override
    public OperationType getOperationType() {
        return opType;
    }
    
    @Override
    public String getUri() {
        //return uriTemplate.getDirectory() + uri + uriTemplate.getExtension();
        return uri;
    }
    
    @Override
    public DocumentMetadataWriteHandle getMetadata() {
        return metadataHandle;
    }
    
    @Override
    public AbstractWriteHandle getContent() {
        return handle;
    }
}
