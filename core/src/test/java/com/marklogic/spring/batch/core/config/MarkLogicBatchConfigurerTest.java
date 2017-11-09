package com.marklogic.spring.batch.core.config;

import com.marklogic.spring.batch.config.MarkLogicBatchConfiguration;
import com.marklogic.spring.batch.test.AbstractSpringBatchTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableBatchProcessing
@ContextConfiguration(classes = {MarkLogicBatchConfiguration.class})
public class MarkLogicBatchConfigurerTest extends AbstractSpringBatchTest {

    private final static Logger logger = LoggerFactory.getLogger(MarkLogicBatchConfigurerTest.class);

    ApplicationContext ctx;

    @Test
    public void doBeansExistTest() throws NoSuchBeanDefinitionException {
        for (String beanName : ctx.getBeanDefinitionNames()) {
            logger.info(beanName);
        }
        Assert.isTrue(ctx.containsBeanDefinition("batchConfigurer"), "Cannot find batchConfigurer bean");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
