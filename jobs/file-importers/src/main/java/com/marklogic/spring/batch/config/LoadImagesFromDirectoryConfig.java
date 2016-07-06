package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.item.DocumentItemWriter;
import com.marklogic.spring.batch.item.file.MlcpFileReader;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.jpeg.JpegParser;
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

public class LoadImagesFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job loadImagesFromDirectoryJob(@Qualifier("loadImagesFromDirectoryJobStep1") Step step1) {
        return jobBuilderFactory.get("loadImagesFromDirectoryJob").start(step1).build();
    }

    @Bean
    @JobScope
    public Step loadImagesFromDirectoryJobStep1(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath,
            @Value("#{jobParameters['input_file_pattern']}") String inputFilePattern) {

        ItemProcessor<Resource, Document> processor = new ItemProcessor<Resource, Document>() {

            private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            @Override
            public Document process(Resource item) throws Exception {
                ContentHandler handler = new ToXMLContentHandler();
                JpegParser parser = new JpegParser();
                Metadata metadata = new Metadata();
                parser.parse(item.getInputStream(), handler, metadata, null);
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(handler.toString())));
                /**
                 * TODO Need to expand this to have more options for setting the URI.
                 */
                document.setDocumentURI(item.getFilename());
                return document;
            }
        };

        return stepBuilderFactory.get("step1")
                .<Resource, Document>chunk(getChunkSize())
                .reader(new MlcpFileReader(inputFilePath, inputFilePattern))
                .processor(processor)
                .writer(new DocumentItemWriter(getDatabaseClient()))
                .build();
    }
}
