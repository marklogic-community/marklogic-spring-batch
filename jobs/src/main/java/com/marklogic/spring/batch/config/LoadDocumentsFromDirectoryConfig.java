package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.DocumentItemWriter;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by sstafford on 6/17/2016.
 */
public class LoadDocumentsFromDirectoryConfig extends AbstractMarkLogicBatchConfig {

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("loadImagesFromDirectoryJob").start(step1).build();
    }

    @Bean
    @JobScope
    protected Step step1(ItemReader<Resource> reader, ItemProcessor<Resource, Document> itemProcessor, ItemWriter<Document> itemWriter) {
        return stepBuilderFactory.get("step1")
                .<Resource, Document>chunk(getChunkSize())
                .reader(reader)
                .processor(itemProcessor)
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<Resource> reader(
            @Value("#{jobParameters['input_file_path']}") String inputFilePath)
    {
        logger.info("READER");
        ResourcesItemReader itemReader = new ResourcesItemReader();
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        try {
            Resource[] resources = ctx.getResources(inputFilePath);
            for (int i = 0; i < resources.length; i++) {
                logger.info(resources[i].getFilename());
                resourceList.add(resources[i]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        itemReader.setResources(resourceList.toArray(new Resource[resourceList.size()]));
        return itemReader;
    }

    @Bean
    public ItemProcessor<Resource, Document> itemProcessor() {
        return new ItemProcessor<Resource, Document>() {

            @Override
            public Document process(Resource item) throws Exception {
                logger.info("Processing images");
                ContentHandler handler = new ToXMLContentHandler();

                AutoDetectParser parser = new AutoDetectParser();
                Metadata metadata = new Metadata();
                parser.parse(item.getInputStream(), handler, metadata);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(handler.toString())));
                document.setDocumentURI("test.xml");
                return document;
            }
        };
    }

    @Bean
    public ItemWriter<Document> itemWriter() {
        return new DocumentItemWriter(getDatabaseClient());
    }
}
