package com.marklogic.spring.batch.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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

import com.fasterxml.jackson.databind.JsonNode;
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
			ItemProcessor<Resource, JsonNode> processor,
			ItemWriter<JsonNode> writer) {
		return stepBuilders.get("step1").<Resource, JsonNode> chunk(10)
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
			ex.printStackTrace();
		}

		itemReader.setResources(resourceList.toArray(new Resource[resourceList
				.size()]));
		return itemReader;
	}

	@Bean
	public ItemProcessor<Resource, JsonNode> processor() {
		return new ItemProcessor<Resource, JsonNode>() {
			@Override
			public JsonNode process(Resource item) throws Exception {
				ObjectNode objectNode = MAPPER.convertValue(item.getFile(),
						ObjectNode.class);
				objectNode.put("uri", new DefaultUriGenerator().generateUri(
						item.getFile(), StringUtils.EMPTY));
				return MAPPER.readTree(objectNode.toString());
			}
		};
	}

	@Bean
	public ItemWriter<JsonNode> writer() {
		return new ItemWriter<JsonNode>() {
			@Override
			public void write(List<? extends JsonNode> items) throws Exception {
				DatabaseClient client = databaseClientProvider
						.getDatabaseClient();
				JSONDocumentManager jsonDocumentManager = client
						.newJSONDocumentManager();
				items.forEach(item -> {
					jsonDocumentManager.write(item.get("uri").textValue(),
							new JacksonHandle(item));
				});
			}
		};
	}

}
