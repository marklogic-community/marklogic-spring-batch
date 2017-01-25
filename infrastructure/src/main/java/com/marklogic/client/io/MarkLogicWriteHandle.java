package com.marklogic.client.io;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.marker.AbstractWriteHandle;
import com.marklogic.client.io.marker.DocumentMetadataWriteHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Use the Java Client DocumentWriteOperationImpl class instead.
 *
 * TODO Remove this before 1.0.0?
 */
@Deprecated
public class MarkLogicWriteHandle implements DocumentWriteOperation {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    private String uri;
    private DocumentMetadataHandle metadataHandle;
    private AbstractWriteHandle handle;
    private OperationType opType;

    public MarkLogicWriteHandle() {

    }
    
    public MarkLogicWriteHandle(String uri, DocumentMetadataHandle metadata, AbstractWriteHandle handle) {
        this.uri = uri;
        this.metadataHandle = metadata;
        this.handle = handle;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public void setMetadataHandle(DocumentMetadataHandle metadataHandle) {
        this.metadataHandle = metadataHandle;
    }
    
    public void setHandle(AbstractWriteHandle handle) {
        this.handle = handle;
    }
    
    @Override
    public OperationType getOperationType() {
        return OperationType.DOCUMENT_WRITE;
    }
    
    @Override
    public String getUri() {
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
    
    @Override
    public String getTemporalDocumentURI() {
        return null;
    }
}
