package com.marklogic.spring.batch.item;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemStreamSupport;

import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.Capability;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;

/**
 * Base class for writing documents. Should be able to support both the Client API and XCC.
 */
public abstract class AbstractDocumentWriter extends ItemStreamSupport {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private UriGenerator uriGenerator = new DefaultUriGenerator();

    private String[] collections;

    // Comma-separated list of role,read,role,update, just like in Client API
    private String permissions;

    protected String generateUri(Object o, String id) {
        return uriGenerator.generateUri(o, id);
    }

    protected DocumentMetadataHandle buildMetadata() {
        DocumentMetadataHandle h = new DocumentMetadataHandle();
        h = h.withCollections(collections);
        if (permissions != null) {
            String[] array = permissions.split(",");
            for (int i = 0; i < array.length; i += 2) {
                h.getPermissions().add(array[i], Capability.valueOf(array[i + 1].toUpperCase()));
            }
        }
        return h;
    }

    public void setCollections(String... collections) {
        this.collections = collections;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    protected UriGenerator getUriGenerator() {
        return uriGenerator;
    }
}