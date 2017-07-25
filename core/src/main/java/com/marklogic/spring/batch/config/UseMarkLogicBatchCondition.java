package com.marklogic.spring.batch.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class UseMarkLogicBatchCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        Boolean enabled = env.getProperty("marklogic.batch.config.enabled", Boolean.class);
        if (enabled == null) {
            return false;
        } else {
            return enabled.booleanValue();
        }

    }

}
