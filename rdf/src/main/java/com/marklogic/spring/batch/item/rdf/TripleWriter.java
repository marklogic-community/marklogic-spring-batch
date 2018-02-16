package com.marklogic.spring.batch.item.rdf;

import com.marklogic.client.DatabaseClient;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.graph.GraphFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * As part of the spring batch process we need an item reader and item writer
 * This class encapsulates and overrides the required methods for the item writer to process the data that
 * has been read from the item reader into a graph of the triple using jena libraries. This graph is inserted
 * into MarkLogic using the MarkLogicDatSetGraph by using the client API. The triples are inserted into MarkLogic
 * based on the chunk size as part of the batch process.
 */
public class TripleWriter extends RdfTripleItemWriter {

    /* Temporary Solution
    *  TODO: Should refer to the Metadata
    * */
    public static String PK_MAP_KEY = "%";
    public static String META_MAP_KEY = "^";

    // Configurable
    private String graphPrefix;
    private Map<String,Graph> graphNodes = new HashMap<>();
    private String baseIri;

    public TripleWriter(DatabaseClient client, String graphPrefix, String baseIri) {
        super(client);
        this.graphPrefix = graphPrefix;
        this.baseIri = baseIri;
    }

    /**
     * So what we need to do is, extract the triple from the map and use
     * writeRecords to insert the triple into the graph
     *
     */
    @Override
    public void write(List<? extends Map<String, Object>> mapList) throws Exception {
        Graph graph = GraphFactory.createDefaultGraph();
        for (Map<String,Object> map : mapList) {
            String currentTable = (String) map.get("_tableName");
            if (!this.graphNodes.containsKey(currentTable)) {
                Node node = NodeFactory.createURI(this.graphPrefix + currentTable);
                dsg.addGraph(node, graph);
                this.graphNodes.put(currentTable, dsg.getGraph(node));
                writeRecords(
                        new Triple(
                            NodeFactory.createURI(baseIri + "#" + currentTable),
                            NodeFactory.createURI(baseIri + "#typeOf"),
                            NodeFactory.createURI(baseIri + "#table")),
                        currentTable
                );
            }
            process (map, currentTable);
        }
    }

    protected List<Triple> process(Map<String, Object> item, String currentTable) throws Exception {
        Map<String, Object> metadata = (Map<String, Object>) item.get(META_MAP_KEY);
        String pk = (String) metadata.get(PK_MAP_KEY);
        List<Triple> triples = new ArrayList<>();

        for (Map.Entry<String, Object> entry : item.entrySet()) {
            if (null != entry.getValue() &&
                    null != entry.getKey() &&
                    !(
                        entry.getKey().equals(META_MAP_KEY) ||
                        entry.getKey().equals("_tableName")
                    )) {

                Node object;
                if ("INTEGER".equals(metadata.get(entry.getKey()))) {
                    object = NodeFactory.createLiteral(entry.getValue().toString(), XSDDatatype.XSDinteger);
                } else if ("DATE".equals(metadata.get(entry.getKey()))) {
                    object = NodeFactory.createLiteral(entry.getValue().toString(), XSDDatatype.XSDdate);
                } else if ("DECIMAL".equals(metadata.get(entry.getKey()))) {
                    object = NodeFactory.createLiteral(entry.getValue().toString(), XSDDatatype.XSDdecimal);
                } else {
                    object = NodeFactory.createLiteral(entry.getValue().toString());
                }

                writeRecords(
                        new Triple(
                            NodeFactory.createURI(baseIri + currentTable + "#" + item.get(pk)),
                            NodeFactory.createURI(baseIri + currentTable + "#has" + entry.getKey()),
                            object
                        ),
                        currentTable
                );
            }
        }
        return triples;
    }

    /**
     * Uses the graph that was created to add the triple and either merges or adds based on the
     * availability of the graph node
     * @param rdfTriple
     */
    private void writeRecords(Triple rdfTriple, String tableName) {
        graphNodes.get(tableName).add(rdfTriple);
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {

    }

    @Override
    public void close() throws ItemStreamException {

    }
}