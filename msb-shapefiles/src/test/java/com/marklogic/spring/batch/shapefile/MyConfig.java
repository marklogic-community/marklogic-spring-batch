package com.marklogic.spring.batch.shapefile;

import com.marklogic.client.helper.DatabaseClientConfig;
import com.marklogic.client.helper.DatabaseClientProvider;
import com.marklogic.client.spring.BasicConfig;
import com.marklogic.client.spring.SimpleDatabaseClientProvider;
import com.marklogic.xcc.template.XccTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by rrudin on 6/24/2016.
 */
@Configuration
@PropertySource({"file:../core/gradle.properties"})
public class MyConfig extends BasicConfig {
}
