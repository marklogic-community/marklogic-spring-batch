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

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MarkLogicBatchConfigurer.class, MarkLogicBatchConfiguration.class, MarkLogicJobRepositoryProperties.class})
@TestPropertySource(properties = { "marklogic.batch.config.enabled=false"})
public class DoNotUseMarkLogicBatchConfigurerTest implements ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(UseMarkLogicBatchConfigurerTest.class);

    ApplicationContext ctx;

    @Test
    public void markLogicBatchConfigurerDoesNotExistTest() throws NoSuchBeanDefinitionException {
        for (String beanName : ctx.getBeanDefinitionNames()) {
            logger.info(beanName);
        }
        assertThat(Arrays.asList(ctx.getBeanDefinitionNames()), not(hasItem("markLogicBatchConfigurer")));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

}
