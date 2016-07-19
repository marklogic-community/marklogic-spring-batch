package example;

import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import example.data.Invoice;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Map;

public class ExtractInvoiceDataToMarkLogicConfig extends AbstractMarkLogicBatchConfig implements EnvironmentAware {
    
    private Environment env;
    
    @Bean
    public Job job(Step step) {
        return jobBuilderFactory.get("extractInvoiceDataFromRdbmsJob").start(step).build();
    }
    
    @Bean
    @JobScope
    public Step step(@Value("#{jobParameters['output_collections']}") String[] collections) {
        
        //Set up ItemReader
        String sql = "SELECT * FROM invoice LEFT JOIN customer on invoice.customerId = customer.id LEFT JOIN item on invoice.id = item.invoiceId LEFT JOIN product on product.id = item.productId ORDER BY invoice.id asc";
        DataSource dataSource = buildDataSource();
        JdbcCursorItemReader<Invoice> reader = new JdbcCursorItemReader<Invoice>();
        reader.setDataSource(dataSource);
        reader.setRowMapper(new InvoiceRowMapper());
        reader.setSql(sql);
        
        ItemProcessor processor = new ItemProcessor<Invoice, InputStreamHandle>() {
    
            @Override
            public InputStreamHandle process(Invoice item) throws Exception {
                InputStreamHandle handle = null;
                return handle;
            }
        };
        ItemWriter writer = null;
        
        return stepBuilderFactory.get("step1")
                .<Invoice, InputStreamHandle>chunk(getChunkSize())
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