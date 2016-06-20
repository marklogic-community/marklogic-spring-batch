package com.marklogic.spring.batch.job;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.DocumentItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.util.ArrayList;

@Configuration
public class LoadXmlDocumentsFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job xmlJob(@Qualifier("xmlStep") Step xmlStep) {
        return jobBuilderFactory.get("loadDocumentsFromDirectoryJob").start(xmlStep).build();
    }

    @Bean
    @JobScope
    protected Step xmlStep(ItemReader<Resource> reader, ItemProcessor<Resource, Document> processor,
                           ItemWriter<Document> writer, @Value("#{jobParameters['chunk'] ?: 100}") Integer chunkSize) {

        return stepBuilderFactory.get("xmlStep")
                .<Resource, Document>chunk(chunkSize)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Resource> reader(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern)
    {
        ResourcesItemReader itemReader = new ResourcesItemReader();
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        try {
            Resource[] resources = ctx.getResources(inputFilePath);
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].getFilename().matches(inputFilePattern)) {
                    resourceList.add(resources[i]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        itemReader.setResources(resourceList.toArray(new Resource[resourceList.size()]));
        return itemReader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Resource, Document> xmlProcessor(
            @Value("#{jobParameters['uri_id']}") String uriId,
            @Value("#{jobParameters['document_type']}") String documentType) {
        return new ItemProcessor<Resource, Document>() {
            @Override
            public Document process(Resource item) throws Exception {
                Source source = new StreamSource(item.getFile());
                DOMResult result = new DOMResult();
                TransformerFactory.newInstance().newTransformer().transform(source, result);
                Document doc = (Document) result.getNode();
                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();
                String expression = uriId;
                Node node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
                doc.setDocumentURI("/" + node.getTextContent());
                return doc;
            }
        };
    }

    @Bean
    public ItemWriter<Document> xmlWriter() {
        return new DocumentItemWriter(getDatabaseClient());
    }

}

