package com.marklogic.spring.batch.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.ResourcesItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.uri.DefaultUriGenerator;

@Configuration
@PropertySource("classpath:job.properties")
@EnableBatchProcessing
@Import(com.marklogic.spring.batch.configuration.MarkLogicBatchConfiguration.class)
public class LoadDocumentsFromDirectoryJob {

    public static final String URI = "uri";
    private final Log logger = LogFactory.getLog(LoadDocumentsFromDirectoryJob.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    Environment env;

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private JobBuilderFactory jobBuilders;

    @Autowired
    private StepBuilderFactory stepBuilders;

    @Autowired
    private DatabaseClientProvider databaseClientProvider;

    private Resource[] resources;

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilders.get("loadDocumentsFromDirectoryJob").start(step1)
                .build();
    }

    @Bean
    protected Step step1(ItemReader<Resource> reader,
                         ItemProcessor<Resource, ObjectNode> processor,
                         ItemWriter<ObjectNode> writer) {
        return stepBuilders.get("step1").<Resource, ObjectNode>chunk(10)
                .reader(reader).processor(processor).writer(writer).build();
    }

    @Bean
    public ItemReader<Resource> reader() {
        ResourcesItemReader itemReader = new ResourcesItemReader();
        ArrayList<Resource> resourceList = new ArrayList<Resource>();
        try {
            resources = ctx.getResources(env.getProperty("input_file_path"));
            String inputFilePattern = env.getProperty("input_file_pattern");
            for (int i = 0; i < resources.length; i++) {
                if (resources[i].getFilename().matches(inputFilePattern)) {
                    resourceList.add(resources[i]);
                }
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        itemReader.setResources(resourceList.toArray(new Resource[resourceList.size()]));
        return itemReader;
    }

    @Bean
    public ItemProcessor<Resource, ObjectNode> processor() {
        return new ItemProcessor<Resource, ObjectNode>() {
            @Override
            public ObjectNode process(Resource item) throws Exception {
                ObjectNode objectNode = MAPPER.readValue(item.getFile(),
                        ObjectNode.class);
                objectNode.put(URI, new DefaultUriGenerator().generateUri(item.getFile(), ""));
                return objectNode;
            }
        };
    }

    @Bean
    public ItemWriter<ObjectNode> writer() {
        return new ItemWriter<ObjectNode>() {
            @Override
            public void write(List<? extends ObjectNode> items) throws Exception {
                DatabaseClient client = databaseClientProvider
                        .getDatabaseClient();
                JSONDocumentManager jsonDocumentManager = client
                        .newJSONDocumentManager();
                items.forEach(item -> {
                    String uri = item.get(URI).textValue();
                    item.remove(URI);
                    jsonDocumentManager.write(uri, new JacksonHandle(item));
                });
            }
        };
    }

}