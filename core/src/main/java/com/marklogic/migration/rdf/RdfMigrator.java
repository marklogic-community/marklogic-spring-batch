package com.marklogic.migration.rdf;

import java.util.HashMap;
import java.util.Map;

import com.marklogic.client.helper.LoggingObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.spring.batch.item.RdfTripleItemReader;
import com.marklogic.spring.batch.item.RdfTripleItemWriter;

public class RdfMigrator extends LoggingObject {

    private DatabaseClient databaseClient;
    private ItemProcessor<Map<String, Object>, Map<String, Object>> itemProcessor;

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private JobLauncher jobLauncher;

    private int chunkSize = 50;
    private boolean useRootLocalNameAsCollection = true;
    
	public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("host", "host", true, "The MarkLogic host");
        options.addOption("port", "port", true, "The MarkLogic port");
        // Need rest-writer role for username
        options.addOption("username", "username", true, "The MarkLogic username");
        options.addOption("password", "password", true, "The MarkLogic password");
        options.addOption("database", "database", true, "The optional MarkLogic database name");
        options.addOption("auth", "authentication", true,
                "The optional MarkLogic authentication to use (either BASIC or DIGEST)");
        options.addOption("rootLocalName", "rootLocalName", true,
                "The local name of the root element in documents that will be written to MarkLogic");
        options.addOption("inputFilePath", "inputFilePath", true, "The ttl file to be ingested");        

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String inputFilePath  = cmd.getOptionValue("inputFilePath");
        String rootLocalName = cmd.getOptionValue("rootLocalName");

        DatabaseClient client;
        String host = cmd.getOptionValue("host");
        Integer port = Integer.parseInt(cmd.getOptionValue("port"));
        String username = cmd.getOptionValue("username");
        String password = cmd.getOptionValue("password");
        Authentication auth = Authentication.DIGEST;
        if (cmd.hasOption("auth")) {
            auth = Authentication.valueOf(cmd.getOptionValue("auth"));
        }
        if (cmd.hasOption("database")) {
            client = DatabaseClientFactory.newClient(host, port, cmd.getOptionValue("database"), username, password,
                    auth);
        } else {
            client = DatabaseClientFactory.newClient(host, port, username, password, auth);
        }

        RdfMigrator migrator = new RdfMigrator(client);

        try {
            migrator.migrate(inputFilePath , rootLocalName);
        } finally {
            client.release();
        }

	}
	
    /**
     * Default constructor will assemble all the Spring Batch components based on sensible defaults.
     */
    public RdfMigrator() {
        initializeDefaultSpringBatchComponents();
        this.itemProcessor = new PassThroughItemProcessor<Map<String, Object>>();
    }

    public RdfMigrator(DatabaseClient databaseClient) {
        this();
        this.databaseClient = databaseClient;
    }

    protected void initializeDefaultSpringBatchComponents() {
        ResourcelessTransactionManager transactionManager = new ResourcelessTransactionManager();
        MapJobRepositoryFactoryBean f = new MapJobRepositoryFactoryBean(transactionManager);
        try {
            f.afterPropertiesSet();
            JobRepository jobRepository = f.getObject();
            this.jobBuilderFactory = new JobBuilderFactory(jobRepository);
            this.stepBuilderFactory = new StepBuilderFactory(jobRepository, transactionManager);
            SimpleJobLauncher jbl = new SimpleJobLauncher();
            jbl.setJobRepository(jobRepository);
            jbl.afterPropertiesSet();
            this.jobLauncher = jbl;
        } catch (Exception ex) {
            throw new RuntimeException("Unable to initialize RdfMigrator, cause: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Does all the migration work.
     * 
     * @param fileName
     *            the fileName to execute
     * @param rootLocalName
     *            the local name for the graph or collection that's inserted into MarkLogic
     * @param collections
     *            optional array of collections to add each document to
     */
    public void migrate(String fileName, String rootLocalName, String... collections) {
    	RdfTripleItemReader<Map<String, Object>> reader = new RdfTripleItemReader<Map<String, Object>>();
    	reader.setFileName(fileName);

    	RdfTripleItemWriter writer = new RdfTripleItemWriter(databaseClient, rootLocalName);
        if (useRootLocalNameAsCollection && (collections == null || collections.length == 0)) {
            writer.setCollections(rootLocalName);
        } else {
            writer.setCollections(collections);
        }

        TaskletStep step = stepBuilderFactory.get("migrationStep-" + System.currentTimeMillis())
                .<Map<String, Object>, Map<String, Object>> chunk(chunkSize).reader(reader).processor(itemProcessor)
                .writer(writer).build();

        Job job = jobBuilderFactory.get("migrationJob-" + System.currentTimeMillis()).start(step).build();

        Map<String, JobParameter> parameters = new HashMap<>();
        parameters.put("fileName", new JobParameter(fileName));
        JobParameters jobParams = new JobParameters(parameters);

        try {
            jobLauncher.run(job, jobParams);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }    
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }


    public void setDatabaseClient(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    public void setStepBuilderFactory(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public void setUseRootLocalNameAsCollection(boolean useRootElementNameAsCollection) {
        this.useRootLocalNameAsCollection = useRootElementNameAsCollection;
    }

    public ItemProcessor<Map<String, Object>, Map<String, Object>> getItemProcessor() {
        return itemProcessor;
    }

    public void setItemProcessor(ItemProcessor<Map<String, Object>, Map<String, Object>> itemProcessor) {
        this.itemProcessor = itemProcessor;
    }
    
}
