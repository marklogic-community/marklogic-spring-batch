package example;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.io.*;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
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
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.w3c.dom.Document;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
        UriGenerator uriGenerator = new DefaultUriGenerator();
        
        ItemProcessor processor = new ItemProcessor<Invoice, DocumentWriteOperation>() {
    
            @Override
            public DocumentWriteOperation process(Invoice item) throws Exception {
                JAXBContext jc = JAXBContext.newInstance(example.data.Invoice.class);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.newDocument();
                Marshaller m = jc.createMarshaller();
                m.marshal( item, doc );
                DOMHandle handle = new DOMHandle(doc);
                
                String uri = "/test/invoice_" + uriGenerator.generate() + ".xml";
                
                DocumentMetadataHandle metadata = new DocumentMetadataHandle();
                metadata.withCollections("invoice");
                
                return new MarkLogicWriteHandle(uri, metadata, handle);
            }
        };
        ItemWriter writer = new MarkLogicItemWriter(getDatabaseClient());
        
        return stepBuilderFactory.get("step1")
                .<Invoice, DocumentWriteOperation>chunk(getChunkSize())
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