package example;

import com.marklogic.spring.batch.config.AbstractMarkLogicBatchConfig;
import com.sun.org.apache.xerces.internal.impl.xpath.XPath;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

public class YourJob extends AbstractMarkLogicBatchConfig implements EnvironmentAware {
    
    private Environment env;
    
    @Bean
    public Job extractCommentsFromDatabaseConfigJob(@Qualifier("step1") XPath.Step step1) {
        Step step = null;
        return jobBuilderFactory.get("extractCommentsFromDatabase").start(step).build();
    }

        
        @Override
    public void setEnvironment(Environment environment) {
        this.env = environment;
    }
}