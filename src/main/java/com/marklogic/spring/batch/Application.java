package com.marklogic.spring.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.marklogic.spring.batch.config" })
public class Application {

    public static void main(String[] args) {
    	System.out.println("Starting...");
        SpringApplication.run(Application.class, args);
        System.out.println("Ending...");
    }
    

}