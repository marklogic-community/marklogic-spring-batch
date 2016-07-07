package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.item.DocumentItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.UUID;

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
            @Value("#{jobParameters['format']}") String format,
            @Value("#{jobParameters['root_local_name']}") String rootLocalName,
            @Value("#{jobParameters['collections']}") String[] collections) {

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

        ItemProcessor<SQLXML, Document> processor = new ItemProcessor<SQLXML, Document>() {

            @Override
            public Document process(SQLXML item) throws Exception {
                InputStream binaryStream = item.getBinaryStream();
                DocumentBuilder parser =
                        DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document result = parser.parse(binaryStream);
                result.setDocumentURI(UUID.randomUUID().toString());
                return result;
            }
        };

        ItemWriter<Document> writer = new DocumentItemWriter(getDatabaseClient());

        return stepBuilderFactory.get("step1")
                .<SQLXML, Document>chunk(getChunkSize())
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
