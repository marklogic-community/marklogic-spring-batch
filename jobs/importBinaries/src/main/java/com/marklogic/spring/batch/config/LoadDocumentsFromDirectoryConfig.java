package com.marklogic.spring.batch.config;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.MarkLogicWriteHandle;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import com.marklogic.spring.batch.item.file.EnhancedResourcesItemReader;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

/**
 * TODO Is this needed when we have ImportDocumentsFromDirectoryConfig? This seemingly should at least
 * be renamed to capture its scope, which is to use Apache Tika to process documents.
 */
public class LoadDocumentsFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job loadDocumentsFromDirectoryJob(@Qualifier("loadDocumentsFromDirectoryJobStep1") Step step) {
        return jobBuilderFactory.get("loadImagesFromDirectoryJob").start(step).build();
    }

    @Bean
    @JobScope
    public Step loadDocumentsFromDirectoryJobStep1(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern) {

        ItemProcessor<Resource, DocumentWriteOperation> processor = new ItemProcessor<Resource, DocumentWriteOperation>() {
            @Override
            public DocumentWriteOperation process(Resource item) throws Exception {
                ContentHandler handler = new ToXMLContentHandler();
                AutoDetectParser parser = new AutoDetectParser();
                Metadata metadata = new Metadata();
                parser.parse(item.getInputStream(), handler, metadata);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(handler.toString())));
                document.setDocumentURI(item.getFilename());
                return new MarkLogicWriteHandle(item.getFilename(), new DocumentMetadataHandle(), new DOMHandle(document));
            }
        };

        return stepBuilderFactory.get("step1")
                .<Resource, DocumentWriteOperation>chunk(getChunkSize())
                .reader(new EnhancedResourcesItemReader(inputFilePath, inputFilePattern))
                .processor(processor)
                .writer(new MarkLogicItemWriter(getDatabaseClient()))
                .build();
    }
}
