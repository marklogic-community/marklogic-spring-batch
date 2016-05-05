package com.marklogic.spring.batch;

import java.util.List;

import org.jdom2.Namespace;

import com.marklogic.junit.MarkLogicNamespaceProvider;
import com.marklogic.spring.batch.core.MarkLogicSpringBatch;

public class SpringBatchNamespaceProvider extends MarkLogicNamespaceProvider {
	
	@Override
    protected List<Namespace> buildListOfNamespaces() {
        List<Namespace> list = super.buildListOfNamespaces();
        list.add(Namespace.getNamespace("geo", "http://geonames.org"));
        list.add(Namespace.getNamespace(MarkLogicSpringBatch.JOB_EXECUTION_NAMESPACE_PREFIX, MarkLogicSpringBatch.JOB_EXECUTION_NAMESPACE));
        list.add(Namespace.getNamespace(MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE_PREFIX, MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE));
        list.add(Namespace.getNamespace(MarkLogicSpringBatch.STEP_EXECUTION_NAMESPACE_PREFIX, MarkLogicSpringBatch.STEP_EXECUTION_NAMESPACE));
        list.add(Namespace.getNamespace(MarkLogicSpringBatch.JOB_PARAMETER_NAMESPACE_PREFIX, MarkLogicSpringBatch.JOB_PARAMETER_NAMESPACE));
        list.add(Namespace.getNamespace(MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE_PREFIX, MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE));
        list.add(Namespace.getNamespace("xs", "http://www.w3.org/2001/XMLSchema"));  
        list.add(Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));  
        list.add(Namespace.getNamespace("search", "http://marklogic.com/appservices/search"));        
        return list;
    }
}
