package com.marklogic.migration.sql;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.DatabaseClientFactory.Authentication;
import com.marklogic.client.helper.LoggingObject;
import com.marklogic.spring.batch.columnmap.PathAwareColumnMapProcessor;
import com.marklogic.spring.batch.item.ColumnMapItemWriter;

/**
 * Wraps Spring Batch and provides a bunch of configuration options.
 */
public class SqlMigrator extends LoggingObject {

    private DataSource dataSource;
    private DatabaseClient databaseClient;
    private RowMapper<Map<String, Object>> rowMapper;
    private ItemProcessor<Map<String, Object>, Map<String, Object>> itemProcessor;

    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private JobLauncher jobLauncher;

    private int chunkSize = 100;
    private boolean useRootLocalNameAsCollection = true;

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("host", "host", true, "The MarkLogic host");
        options.addOption("port", "port", true, "The MarkLogic port");
        options.addOption("username", "username", true, "The MarkLogic username");
        options.addOption("password", "password", true, "The MarkLogic password");
        options.addOption("database", "database", true, "The optional MarkLogic database name");
        options.addOption("auth", "authentication", true,
                "The optional MarkLogic authentication to use (either BASIC or DIGEST)");
        options.addOption("jdbcDriver", "jdbcDriver", true, "The fully-qualified class name of the JDBC driver");
        options.addOption("jdbcUrl", "jdbcUrl", true, "The JDBC URL");
        options.addOption("jdbcUsername", "jdbcUsername", true, "The JDBC username");
        options.addOption("jdbcPassword", "jdbcPassword", true, "The JDBC password");
        options.addOption("sql", "sqlQuery", true, "The SQL query to execute");
        options.addOption("rootLocalName", "rootLocalName", true,
                "The local name of the root element in documents that will be written to MarkLogic");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        String sql = cmd.getOptionValue("sql");
        String rootLocalName = cmd.getOptionValue("rootLocalName");

        DriverManagerDataSource dmds = new DriverManagerDataSource();
        dmds.setDriverClassName(cmd.getOptionValue("jdbcDriver"));
        dmds.setUrl(cmd.getOptionValue("jdbcUrl"));
        dmds.setUsername(cmd.getOptionValue("jdbcUsername"));
        dmds.setPassword(cmd.getOptionValue("jdbcPassword"));

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

        SqlMigrator migrator = new SqlMigrator(dmds, client);

        try {
            migrator.migrate(sql, rootLocalName);
        } finally {
            client.release();
        }
    }

    /**
     * Default constructor will assemble all the Spring Batch components based on sensible defaults.
     */
    public SqlMigrator() {
        initializeDefaultSpringBatchComponents();
        this.rowMapper = new ColumnMapRowMapper();
        this.itemProcessor = new PathAwareColumnMapProcessor();
    }

    public SqlMigrator(DataSource dataSource, DatabaseClient databaseClient) {
        this();
        this.dataSource = dataSource;
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
            throw new RuntimeException("Unable to initialize SqlMigrator, cause: " + ex.getMessage(), ex);
        }
    }

    /**
     * Does all the migration work.
     * 
     * @param sql
     *            the SQL query to execute
     * @param rootLocalName
     *            the local name for the root element in the XML document that's inserted into MarkLogic
     * @param collections
     *            optional array of collections to add each document to
     */
    public void migrate(String sql, String rootLocalName, String... collections) {
        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setRowMapper(rowMapper);
        reader.setSql(sql);

        ColumnMapItemWriter writer = new ColumnMapItemWriter(databaseClient, rootLocalName);
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
        parameters.put("SQL", new JobParameter(sql));
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

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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

    public void setRowMapper(RowMapper<Map<String, Object>> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public ItemProcessor<Map<String, Object>, Map<String, Object>> getItemProcessor() {
        return itemProcessor;
    }

    public void setItemProcessor(ItemProcessor<Map<String, Object>, Map<String, Object>> itemProcessor) {
        this.itemProcessor = itemProcessor;
    }

}
