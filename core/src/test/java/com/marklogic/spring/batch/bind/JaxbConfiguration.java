package com.marklogic.spring.batch.bind;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class JaxbConfiguration {

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(
                com.marklogic.spring.batch.core.AdaptedJobExecution.class,
                com.marklogic.spring.batch.core.AdaptedJobInstance.class,
                com.marklogic.spring.batch.core.AdaptedJobParameters.class,
                com.marklogic.spring.batch.core.AdaptedStepExecution.class,
                com.marklogic.spring.batch.core.AdaptedExecutionContext.class);
        marshaller.setAdapters(
                new ExecutionContextAdapter(),
                new JobExecutionAdapter(),
                new JobInstanceAdapter(),
                new JobParametersAdapter(),
                new StepExecutionAdapter());
        //marshaller.setMarshallerProperties(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

}
