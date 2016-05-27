package com.marklogic.spring.batch.item;

import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.graph.GraphFactory;
import com.marklogic.client.DatabaseClient;
import com.marklogic.semantics.jena.MarkLogicDatasetGraph;
import com.marklogic.semantics.jena.MarkLogicDatasetGraphFactory;

public class RdfTripleItemWriter extends AbstractDocumentWriter implements ItemWriter<Map<String, Object>>, ItemStream {
	
    // Configurable
    private String rootElementName;
    private Node graphNode;
    private MarkLogicDatasetGraph dsg;
    private DatabaseClient client;

    public RdfTripleItemWriter(DatabaseClient client, String rootElementName) {
        this.client = client;
    	this.dsg = getMarkLogicDatasetGraph(client);
        this.rootElementName = rootElementName;
        graphNode = NodeFactory.createURI(rootElementName);
        // Clear the triples- temporary
        dsg.clear();
    }

    /**
     * So what we need to do is, given an ID, we need to see if there's already a Map for that ID. If there is, we need
     * to merge the data from the new item into the existing item.
     * 
     * When we get a column label like address/street, we need to tokenize it...
     */
    @Override
    public void write(List<? extends Map<String, Object>> items) throws Exception {
        Graph graph = GraphFactory.createDefaultGraph();
        logger.info("writing triple records");
        for (Map<String, Object> columnMap : items) {
            String idKey = columnMap.keySet().iterator().next();
            Object id = columnMap.get(idKey);
            logger.debug("KEY [" + idKey + "]");
            writeRecords((Triple)id, graph);
        }
    }

    private void writeRecords(Triple rdfTriple, Graph graph) {
    	// Add triple to the graph
    	graph.add(rdfTriple);
        if (dsg.containsGraph(graphNode))
        {
        	logger.debug("Yes, we have this graphNode in MarkLogic");
        	logger.debug("Triple [" + rdfTriple.toString() + "]");
        	dsg.mergeGraph(graphNode, graph);
        }
        else
        {   
        	logger.debug("Store the graph in MarkLogic.");
        	dsg.addGraph(graphNode, graph);
        }
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (graphNode == null) {
            graphNode = NodeFactory.createURI(rootElementName);
        }

        if (this.dsg == null) {
        	this.dsg = getMarkLogicDatasetGraph(client);
        }
    }

    /**
     * This close method from ItemStream gives us a way to write all the remaining records in our map after all the rows
     * have been read from the Triple data file.
     */
    @Override
    public void close() throws ItemStreamException {
        if (logger.isDebugEnabled()) {
            logger.debug("Closing Writer, and writing remaining records");
        }
        //writeRecords(null, null);
    }	
    
    private MarkLogicDatasetGraph getMarkLogicDatasetGraph(DatabaseClient client)
    {
        MarkLogicDatasetGraph dataSetGraph = MarkLogicDatasetGraphFactory
                .createDatasetGraph(client);    
        return dataSetGraph;
    }
    
    protected void clearTripleData()
    {
    	dsg.clear();
    }

}

