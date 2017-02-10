package com.marklogic.spring.batch.item.processor;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.marker.AbstractWriteHandle;

import java.util.UUID;

public abstract class AbstractMarkLogicItemProcessor<T> implements MarkLogicItemProcessor<T> {

    // Expected to be role,capability,role,capability,etc.
    private String[] permissions;
    private String[] collections;
    private String type = "document";

    public DocumentWriteOperation process(T item) throws Exception {
        return new DocumentWriteOperationImpl(
                DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                getUri(item),
                getDocumentMetadata(item),
                getContentHandle(item));
    }

    public String getUri(T item) {
        return "/" + getType() + UUID.randomUUID().toString();
    }

    public abstract AbstractWriteHandle getContentHandle(T item) throws Exception;

    protected DocumentMetadataHandle getDocumentMetadata(T item) {
        DocumentMetadataHandle metadata = new DocumentMetadataHandle();
        metadata.withCollections(collections);
        if (permissions != null) {
            for (int i = 0; i < permissions.length; i += 2) {
                String role = permissions[i];
                DocumentMetadataHandle.Capability c = DocumentMetadataHandle.Capability.valueOf(permissions[i + 1].toUpperCase());
                metadata.withPermission(role, c);
            }
        }
        return metadata;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public String[] getCollections() {
        return collections;
    }

    public void setCollections(String[] collections) {
        this.collections = collections;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
