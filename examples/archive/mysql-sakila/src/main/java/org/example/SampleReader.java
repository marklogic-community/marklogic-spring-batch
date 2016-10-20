package org.example;

import org.springframework.batch.item.*;
import org.w3c.dom.Document;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;

public class SampleReader extends ItemStreamSupport implements ItemReader<Document> {

    private int count;
    private int counter = 0;
    private Transformer transformer;

    public SampleReader(int count) throws Exception {
        this.count = count;
        this.transformer = TransformerFactory.newInstance().newTransformer();
    }

    @Override
    public Document read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (counter > count) {
            return null;
        }
        counter++;

        String xml = "<test><number>" + counter + "</number></test>";
        Source source = new StreamSource(new StringReader(xml));
        DOMResult result = new DOMResult();
        transformer.transform(source, result);
        Document doc = (Document) result.getNode();
        doc.setDocumentURI("/test/" + counter + ".xml");
        return doc;
    }
}
