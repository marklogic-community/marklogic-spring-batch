package org.example;

import com.marklogic.spring.batch.core.MarkLogicSpringBatch;
import com.marklogic.spring.batch.test.SpringBatchNamespaceProvider;
import org.jdom2.Namespace;

import java.util.List;

/**
 * Registers commonly used namespaces for marklogic-spring-batch tests.
 */
public class GeonamesNamespaceProvider extends SpringBatchNamespaceProvider {

    @Override
    protected List<Namespace> buildListOfNamespaces() {
        List<Namespace> list = super.buildListOfNamespaces();
        list.add(Namespace.getNamespace("geo", "http://geonames.org"));
        return list;
    }
}
