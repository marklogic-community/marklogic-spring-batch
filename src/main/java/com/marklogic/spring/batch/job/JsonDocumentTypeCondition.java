package com.marklogic.spring.batch.job;

import com.marklogic.client.io.Format;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by sanjuthomas on 5/24/16.
 */
public class JsonDocumentTypeCondition  implements Condition{

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String documentType = context.getEnvironment().getProperty("document_type");
        return Format.JSON.name().equalsIgnoreCase(documentType);
    }

}
