package example;

import com.marklogic.spring.batch.Options;
import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

public class ExtractNorthwindsDataJob extends AbstractMarkLogicBatchConfig implements EnvironmentAware {
    
    private Environment env;
    
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