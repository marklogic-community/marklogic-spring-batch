package com.marklogic.spring.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.marklogic.spring.batch.config" })
public class Application {
	
	private static Log log = LogFactory.getLog(Application.class);
	
	public static void main(String[] args) {
    	//log.debug("Starting...");
        SpringApplication.run(Application.class, args);
        //log.debug("Ending...");
    }
    

}