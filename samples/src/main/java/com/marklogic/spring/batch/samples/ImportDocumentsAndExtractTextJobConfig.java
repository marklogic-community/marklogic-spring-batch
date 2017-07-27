package com.marklogic.spring.batch.samples;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.impl.DocumentWriteOperationImpl;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.spring.batch.item.writer.MarkLogicItemWriter;
import com.marklogic.spring.batch.item.reader.EnhancedResourcesItemReader;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@EnableBatchProcessing
@Import(value = {com.marklogic.spring.batch.config.MarkLogicBatchConfiguration.class})
public class ImportDocumentsAndExtractTextJobConfig {

    @Bean
    public Job loadDocumentsFromDirectoryJob(
        JobBuilderFactory jobBuilderFactory,
        @Qualifier("loadDocumentsFromDirectoryJobStep1") Step step) {
        return jobBuilderFactory.get("loadImagesFromDirectoryJob").start(step).build();
    }

    @Bean
    @JobScope
    public Step loadDocumentsFromDirectoryJobStep1(
            StepBuilderFactory stepBuilderFactory,
            DatabaseClientProvider databaseClientProvider,
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
                DOMHandle handle = new DOMHandle(document);
                handle.setFormat(Format.XML);
                return new DocumentWriteOperationImpl(
                        DocumentWriteOperation.OperationType.DOCUMENT_WRITE,
                        item.getFilename() + ".xml",
                        new DocumentMetadataHandle().withCollections("binary"),
                        handle);
            }
        };
        MarkLogicItemWriter writer = new MarkLogicItemWriter(databaseClientProvider.getDatabaseClient());
        return stepBuilderFactory.get("step1")
                .<Resource, DocumentWriteOperation>chunk(10)
                .reader(new EnhancedResourcesItemReader(inputFilePath, inputFilePattern))
                .processor(processor)
                .writer(writer)
                .build();
    }
}
