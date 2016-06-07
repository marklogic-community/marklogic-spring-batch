package com.marklogic.spring.batch;

import com.marklogic.junit.MarkLogicNamespaceProvider;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;
import org.jdom2.Namespace;

import java.util.List;

/**
 * Had to duplicate this under jobs because it wasn't found under core.
 */
public class SpringBatchNamespaceProvider extends MarkLogicNamespaceProvider {
	
	@Override
    protected List<Namespace> buildListOfNamespaces() {
        List<Namespace> list = super.buildListOfNamespaces();
        list.add(Namespace.getNamespace("geo", "http://geonames.org"));
        list.add(Namespace.getNamespace(MarkLogicSpringBatch.JOB_NAMESPACE_PREFIX, MarkLogicSpringBatch.JOB_NAMESPACE));
        list.add(Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema"));  
        list.add(Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));  
        list.add(Namespace.getNamespace("search", "http://marklogic.com/appservices/search"));        
        return list;
    }
}
