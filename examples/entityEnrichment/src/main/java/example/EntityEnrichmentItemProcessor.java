package example;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.CountedDistinctValue;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;

public class EntityEnrichmentItemProcessor implements ItemProcessor<CountedDistinctValue, String[]> {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DatabaseClient databaseClient;
    private DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private final String TOKENIZER_MODEL_FILE_PATH;
    private final String NAMED_ENTITY_FILE_PATH;
    
    public EntityEnrichmentItemProcessor(
        DatabaseClient client,
        String tokenizerModelFilePath,
        String namedEntityModelFilePath) {
        this.databaseClient = client;
        this.TOKENIZER_MODEL_FILE_PATH = tokenizerModelFilePath;
        this.NAMED_ENTITY_FILE_PATH = namedEntityModelFilePath;
    }

    //Assumes that the item being passed in is a document uri
    @Override
    public String[] process(CountedDistinctValue item) throws Exception {
        XMLDocumentManager docMgr = databaseClient.newXMLDocumentManager();
        String uri = item.get("xs:string", String.class);
        StringHandle handle = docMgr.read(uri, new StringHandle());
        InputStream tokenModel = new FileInputStream(TOKENIZER_MODEL_FILE_PATH);
        TokenizerModel model = new TokenizerModel(tokenModel);
        Tokenizer tokenizer = new TokenizerME(model);
        String[] tokens = tokenizer.tokenize(handle.get());
        
        InputStream namedEntityModel = new FileInputStream(NAMED_ENTITY_FILE_PATH);
        TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(namedEntityModel);
        NameFinderME nameFinder = new NameFinderME(nameFinderModel);
        
        Span[] spans = nameFinder.find(tokens);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.newDocument();
        
        Element root = doc.createElement("nameFinder");
        doc.appendChild(root);
        
        StringBuilder stringBuilder = new StringBuilder("");
        for ( Span s : spans ) {
            logger.debug("Token Start: " + Integer.toString(s.getStart()));
            logger.debug("Token End: " + Integer.toString(s.getEnd()));
            String name = "";
            for (int i = s.getStart(); i < s.getEnd(); i++) {
                name = name + tokens[i] + " ";
            }
            name = name.substring(0, name.length()-1);
            logger.info(name);
            Element elName = doc.createElement("name");
            elName.setTextContent(name);
            root.appendChild(elName);
        }
    
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
        
        
        String[] info = new String[2];
        info[0] = uri;
        info[1] = output;
        return info;
    }
    
}

