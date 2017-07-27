package com.marklogic.spring.batch.config.support;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition for determining whether the MarkLogic JobRepository implementation should be enabled.
 */
public class MarkLogicJobRepositoryCondition implements Condition {

    /**
     * jrPassword is checked since that's the only one that's required - the others all have
     * sensible default values.
     *
     * @param context
     * @param metadata
     * @return
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty(Options.JOB_REPOSITORY_PASSWORD) != null;
    }
}
