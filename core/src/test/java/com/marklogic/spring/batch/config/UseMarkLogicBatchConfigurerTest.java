package com.marklogic.spring.batch.config;

import com.marklogic.spring.batch.core.repository.support.MarkLogicJobRepositoryProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MarkLogicBatchConfigurer.class, MarkLogicBatchConfiguration.class, MarkLogicJobRepositoryProperties.class})
@TestPropertySource(properties = { "marklogic.batch.config.enabled=true"})
public class UseMarkLogicBatchConfigurerTest implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(UseMarkLogicBatchConfigurerTest.class);

    ApplicationContext ctx;

    @Test
    public void doBeansExistTest() throws NoSuchBeanDefinitionException {
        for (String beanName : ctx.getBeanDefinitionNames()) {
            logger.info(beanName);
        }
        Assert.isTrue(ctx.containsBeanDefinition("markLogicBatchConfigurer"), "Cannot find batchConfigurer bean");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
