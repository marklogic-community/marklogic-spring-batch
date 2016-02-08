package com.marklogic.spring.batch;

import java.util.List;

import org.jdom2.Namespace;

import com.marklogic.junit.MarkLogicNamespaceProvider;

public class SpringBatchNamespaceProvider extends MarkLogicNamespaceProvider {
	
	@Override
    protected List<Namespace> buildListOfNamespaces() {
        List<Namespace> list = super.buildListOfNamespaces();
        list.add(Namespace.getNamespace("geo", "http://geonames.org"));
        list.add(Namespace.getNamespace("sb", "http://projects.spring.io/spring-batch"));
        return list;
    }
}
