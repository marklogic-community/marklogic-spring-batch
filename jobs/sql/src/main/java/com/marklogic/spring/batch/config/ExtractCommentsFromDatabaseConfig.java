package com.marklogic.spring.batch.config;

import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.item.InputStreamHandleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;

public class ExtractCommentsFromDatabaseConfig extends AbstractMarkLogicBatchConfig implements EnvironmentAware {

    private Environment env;

    @Bean
    public Job extractUsersFromDatabaseConfigJob(@Qualifier("step1") Step step1) {
        return jobBuilderFactory.get("extractUsersFromDatabase").start(step1).build();
    }

    @Bean
    @JobScope
    public Step step1(
            @Value("#{jobParameters['sql']}") String sql,
            @Value("#{jobParameters['output_collections']}") String[] collections) {

        //Set up ItemReader
        RowMapper<SQLXML> mapper = new RowMapper<SQLXML>() {
            @Override
            public SQLXML mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getSQLXML(1);
            }
        };
        DataSource dataSource = buildDataSource();
        JdbcCursorItemReader<SQLXML> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setRowMapper(mapper);
        reader.setSql(sql);

        ItemProcessor<SQLXML, InputStreamHandle> processor = new ItemProcessor<SQLXML, InputStreamHandle>() {

            @Override
            public InputStreamHandle process(SQLXML item) throws Exception {
                InputStream binaryStream = item.getBinaryStream();
                InputStreamHandle handle = new InputStreamHandle(binaryStream);
                handle.setFormat(Format.XML);
                return handle;
            }
        };

        InputStreamHandleItemWriter writer = new InputStreamHandleItemWriter(getDatabaseClient());
        writer.setCollections(collections);

        return stepBuilderFactory.get("step1")
                .<SQLXML, InputStreamHandle>chunk(getChunkSize())
                .reader(reader)
                .processor(processor)
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

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }


}
