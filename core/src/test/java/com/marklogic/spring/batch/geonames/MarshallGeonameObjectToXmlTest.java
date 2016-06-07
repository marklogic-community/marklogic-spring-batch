package com.marklogic.spring.batch.geonames;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilderFactory;

import com.marklogic.junit.NamespaceProvider;
import com.marklogic.junit.spring.AbstractSpringTest;
import com.marklogic.spring.batch.SpringBatchNamespaceProvider;
import org.geonames.Geoname;
import org.jdom2.input.DOMBuilder;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.w3c.dom.Document;

import com.marklogic.junit.Fragment;

@ContextConfiguration(classes = {
        com.marklogic.client.spring.BasicConfig.class
})
public class MarshallGeonameObjectToXmlTest extends AbstractSpringTest {

    protected NamespaceProvider getNamespaceProvider() {
        return new SpringBatchNamespaceProvider();
    }

    @Test
    public void marshallGeonameTest() throws Exception {
        Geoname geo = new Geoname();
        geo.setId("123");
        List<String> names = new ArrayList<>();
        names.add("Alpha");
        names.add("Beta");
        geo.setNames(names);

        Document w3cDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(org.geonames.Geoname.class).createMarshaller();
        marshaller.marshal(geo, w3cDoc);
        Fragment frag = new Fragment(new DOMBuilder().build(w3cDoc));
        frag.setNamespaces(getNamespaceProvider().getNamespaces());
        frag.prettyPrint();
        frag.assertElementExists("/geo:geoname/geo:names/geo:name[2][text() = 'Beta']");
    }

}
