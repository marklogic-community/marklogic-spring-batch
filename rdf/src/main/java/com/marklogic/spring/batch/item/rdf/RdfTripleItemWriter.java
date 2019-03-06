package com.marklogic.spring.batch.item.rdf;

import java.util.List;
import java.util.Map;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.graph.GraphFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;

import com.marklogic.client.DatabaseClient;
import com.marklogic.semantics.jena.MarkLogicDatasetGraph;
import com.marklogic.semantics.jena.MarkLogicDatasetGraphFactory;

/**
 * As part of the spring batch process we need an item reader and item writer
 * This class encapsulates and overrides the required methods for the item writer to process the data that 
 * has been read from the item reader into a graph of the triple using jena libraries. This graph is inserted 
 * into MarkLogic using the MarkLogicDatSetGraph by using the client API. The triples are inserted into MarkLogic
 * based on the chunk size as part of the batch process.  
 * 
 * @author viyengar
 *
 */
public class RdfTripleItemWriter extends AbstractDocumentWriter implements ItemWriter<Map<String, Object>>, ItemStream {
	
    // Configurable
    private String graphName;
    private Node graphNode;
    private MarkLogicDatasetGraph dsg;
    private DatabaseClient client;

    public RdfTripleItemWriter(DatabaseClient client, String graphName) {
        this.client = client;
    	this.dsg = getMarkLogicDatasetGraph(client);
        this.graphName = graphName;
        graphNode = NodeFactory.createURI(graphName);
        // Clear the triples- temporary
        dsg.clear();
    }

    /**
     * So what we need to do is, extract the triple from the map and use
     * writeRecords to insert the triple into the graph
     * 
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
        logger.info("Triple inserted count [" + this.getTripleCount() + "]");
    }

    /**
     * Uses the graph that was created to add the triple and either merges or adds based on the 
     * availability of the graph node
     * @param rdfTriple
     * @param graph
     */
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

    /** 
     * Make sure the graph node and MarkLogic data set graphs are initialized when the
     * context is opened
     */
    @Override
    public void open(ExecutionContext executionContext) {
        if (graphNode == null) {
            graphNode = NodeFactory.createURI(graphName);
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
    
    /**
     * Returns the triple count for the graph that was inserted into MarkLogic
     * @return
     */
    public int getTripleCount()
    {
    	int tripleCount = 0;
    	if (dsg.containsGraph(graphNode))
    	{
    		tripleCount = dsg.getGraph(graphNode).size();
    	}
    	return tripleCount;
    }
    
    /**
     * Retrieve the MarkLogic data set graph using the graph factory for the client
     * @param client
     * @return MarkLogicDatasetGraph
     */
    private MarkLogicDatasetGraph getMarkLogicDatasetGraph(DatabaseClient client)
    {
        MarkLogicDatasetGraph dataSetGraph = MarkLogicDatasetGraphFactory
                .createDatasetGraph(client);    
        return dataSetGraph;
    }
    
    /**
     * Clears the triples data in MarkLogic
     */
    protected void clearTripleData()
    {
    	dsg.clear();
    }

}

