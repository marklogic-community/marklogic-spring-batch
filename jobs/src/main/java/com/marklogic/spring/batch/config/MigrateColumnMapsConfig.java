package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.Main;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.columnmap.PathAwareColumnMapProcessor;
import com.marklogic.spring.batch.configuration.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.configuration.OptionParserConfigurer;
import com.marklogic.spring.batch.item.ColumnMapItemWriter;
import joptsimple.OptionParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configuration for a simple approach for migrating rows to documents via Spring JDBC ColumnMaps.
 */
@Configuration
public class MigrateColumnMapsConfig extends AbstractMarkLogicBatchConfig implements OptionParserConfigurer{

    public static void main(String[] args) throws Exception {
        Main.main(new String[]{"--help", "--config", "com.marklogic.spring.batch.config.MigrateColumnMapsConfig"});
    }

    @Autowired
    private Environment env;

    @Override
    public void configureOptionParser(OptionParser parser) {
        parser.accepts("sql", "The SQL query for selecting rows to migrate").withRequiredArg();
        parser.accepts("rootLocalName", "Name of the root element in each document written to MarkLogic").withRequiredArg();
        parser.accepts("collections", "Comma-separated list of collections to add each document to").withRequiredArg();
    }

    @Bean
    public Job job(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("migrateRowsViaColumnMapsConfig").start(step1).build();
    }

    @Bean
    @JobScope
    public Step step1(
            @Value("#{jobParameters['sql']}") String sql,
            @Value("#{jobParameters['rootLocalName']}") String rootLocalName,
            @Value("#{jobParameters['collections']}") String[] collections) {

        DataSource dataSource = buildDataSource();

        JdbcCursorItemReader<Map<String, Object>> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setRowMapper(new ColumnMapRowMapper());
        reader.setSql(sql);

        ColumnMapItemWriter writer = new ColumnMapItemWriter(getDatabaseClient(), rootLocalName);
        if (collections == null || collections.length == 0) {
            writer.setCollections(rootLocalName);
        } else {
            writer.setCollections(collections);
        }

        return stepBuilderFactory.get("step1")
                .<Map<String, Object>, Map<String, Object>>chunk(getChunkSize())
                .reader(reader)
                .processor(new PathAwareColumnMapProcessor())
                .writer(writer)
                .build();
    }

    /**
     * Protected so that a different data source can be used.
     */
    protected DataSource buildDataSource() {
        logger.info("Creating simple data source based on JDBC connection options");
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getProperty(Options.JDBC_DRIVER));
        ds.setUrl(env.getProperty(Options.JDBC_URL));
        ds.setUsername(env.getProperty(Options.JDBC_USERNAME));
        ds.setPassword(env.getProperty(Options.JDBC_PASSWORD));
        return ds;
    }

}
