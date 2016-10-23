package com.marklogic.spring.batch.config;

import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.columnmap.JsonColumnMapSerializer;
import com.marklogic.spring.batch.columnmap.PathAwareColumnMapProcessor;
import com.marklogic.spring.batch.config.support.OptionParserConfigurer;
import com.marklogic.spring.batch.item.ColumnMapItemWriter;
import joptsimple.OptionParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configuration for a simple approach for migrating rows to documents via Spring JDBC ColumnMaps.
 */
@EnableBatchProcessing
public class MigrateColumnMapsConfig implements OptionParserConfigurer {

    @Autowired
    private Environment env;

    @Override
    public void configureOptionParser(OptionParser parser) {
        parser.accepts("sql", "The SQL query for selecting rows to migrate").withRequiredArg();
        parser.accepts("format", "The format of the documents written to MarkLogic - either xml or json").withRequiredArg().defaultsTo("xml");
        parser.accepts("rootLocalName", "Name of the root element in each document written to MarkLogic").withRequiredArg();
        parser.accepts("collections", "Comma-separated list of collections to add each document to").withRequiredArg();
    }

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, @Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("migrateRowsViaColumnMapsConfig").start(step1).build();
    }

    @Bean
    @JobScope
    public Step step1(
            StepBuilderFactory stepBuilderFactory,
            DatabaseClientProvider databaseClientProvider,
            @Value("#{jobParameters['sql']}") String sql,
            @Value("#{jobParameters['format']}") String format,
            @Value("#{jobParameters['root_local_name']}") String rootLocalName,
            @Value("#{jobParameters['collections']}") String[] collections,
            @Value("#{jobParameters['transform_name']}") String transformName,
            @Value("#{jobParameters['transform_parameters']}") String transformParameters) {

        DataSource dataSource = buildDataSource();

        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setRowMapper(new ColumnMapRowMapper());
        reader.setSql(sql);

        ColumnMapItemWriter writer = new ColumnMapItemWriter(databaseClientProvider.getDatabaseClient(), rootLocalName);
        if (collections == null || collections.length == 0) {
            String[] coll = {rootLocalName};
            writer.setCollections(coll);
        } else {
            writer.setCollections(collections);
        }
        if ("json".equals(format)) {
            writer.setColumnMapSerializer(new JsonColumnMapSerializer());
        }

        return stepBuilderFactory.get("step1")
                .<Map<String, Object>, Map<String, Object>>chunk(10)
                .reader(reader)
                .processor(new PathAwareColumnMapProcessor())
                .writer(writer)
                .build();
    }

    /**
     * Protected so that a different data source can be used.
     */
    protected DataSource buildDataSource() {
        //logger.info("Creating simple data source based on JDBC connection options");
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getProperty(Options.JDBC_DRIVER));
        ds.setUrl(env.getProperty(Options.JDBC_URL));
        ds.setUsername(env.getProperty(Options.JDBC_USERNAME));
        ds.setPassword(env.getProperty(Options.JDBC_PASSWORD));
        return ds;
    }

}
