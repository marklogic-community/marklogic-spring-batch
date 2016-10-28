package example;

import com.marklogic.client.document.DocumentWriteOperation;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.io.*;
import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.item.MarkLogicItemWriter;
import com.marklogic.uri.DefaultUriGenerator;
import com.marklogic.uri.UriGenerator;
import example.data.EmpDetails;

import example.entities.EmpDetailsView;
import example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.batch.item.database.HibernateCursorItemReader;
import org.springframework.batch.item.ExecutionContext;

@EnableBatchProcessing
public class ExtractHumanResourceDataToMarkLogicConfig implements EnvironmentAware {

    private Environment env;

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, Step step) {
        return jobBuilderFactory.get("extractHumanResourceDataUsingHibernateJob").start(step).build();
    }

    @Bean
    @JobScope
    public Step step(
            StepBuilderFactory stepBuilderFactory,
            DatabaseClientProvider databaseClientProvider,
            @Value("#{jobParameters['output_collections']}") String[] collections) {

        //Set up ItemReader
        HibernateCursorItemReader reader = new HibernateCursorItemReader();
        String hql = "from EmpDetailsView";
        reader.setQueryString(hql);
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        reader.setSessionFactory(sessionFactory);
        reader.setUseStatelessSession(true);
        // Scott, when an ExecutionContext is opened, I get an error saying a context can't be opened because one is already opened.  I don't
        //   know how to close it.  If no ExecutionContext is opened, then the application just hangs and doesn't error or write documents to ML.
        ExecutionContext executionContext = new ExecutionContext();
        reader.open(executionContext);

        UriGenerator uriGenerator = new DefaultUriGenerator();

        ItemProcessor processor = new ItemProcessor<EmpDetailsView, DocumentWriteOperation>() {

            @Override
            public DocumentWriteOperation process(EmpDetailsView empDetails) throws Exception {
                JAXBContext jc = JAXBContext.newInstance(example.entities.EmpDetailsView.class);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.newDocument();
                Marshaller m = jc.createMarshaller();
                m.marshal( empDetails, doc );
                DOMHandle handle = new DOMHandle(doc);

                String uri = "/test/emp_details_view_" + uriGenerator.generate() + ".xml";

                DocumentMetadataHandle metadata = new DocumentMetadataHandle();
                metadata.withCollections("matt");

                return new MarkLogicWriteHandle(uri, metadata, handle);
            }
        };
        ItemWriter writer = new MarkLogicItemWriter(databaseClientProvider.getDatabaseClient());
        System.out.println("MATT: returning step1");
        return stepBuilderFactory.get("step1")
                .<EmpDetailsView, DocumentWriteOperation>chunk(10)
                .reader(reader)
                .processor(processor)
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

    @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}