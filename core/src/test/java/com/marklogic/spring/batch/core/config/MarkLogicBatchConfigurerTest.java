package com.marklogic.spring.batch.core.config;

import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import com.marklogic.spring.batch.config.MarkLogicBatchConfigurer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableBatchProcessing
@ContextConfiguration(classes = { MarkLogicBatchConfigurer.class } )
public class MarkLogicBatchConfigurerTest extends AbstractSpringBatchTest {
    
    ApplicationContext ctx;
    
    @Test
    public void doBeansExistTest() throws NoSuchBeanDefinitionException {
        for (String beanName : ctx.getBeanDefinitionNames()) {
            logger.info(beanName);
        }
        Assert.isTrue(ctx.containsBeanDefinition("markLogicBatchConfigurer"));
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
