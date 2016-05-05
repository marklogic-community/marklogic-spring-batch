@XmlSchema(
	namespace=MarkLogicSpringBatch.JOB_EXECUTION_NAMESPACE,
	elementFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,	
	xmlns = {
		@XmlNs(prefix = MarkLogicSpringBatch.JOB_EXECUTION_NAMESPACE_PREFIX, namespaceURI=MarkLogicSpringBatch.JOB_EXECUTION_NAMESPACE),
		@XmlNs(prefix = MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE_PREFIX, namespaceURI=MarkLogicSpringBatch.JOB_INSTANCE_NAMESPACE),
		@XmlNs(prefix = MarkLogicSpringBatch.STEP_EXECUTION_NAMESPACE_PREFIX, namespaceURI=MarkLogicSpringBatch.STEP_EXECUTION_NAMESPACE),
		@XmlNs(prefix = MarkLogicSpringBatch.JOB_PARAMETER_NAMESPACE_PREFIX, namespaceURI=MarkLogicSpringBatch.JOB_PARAMETER_NAMESPACE),
		@XmlNs(prefix = MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE_PREFIX, namespaceURI=MarkLogicSpringBatch.EXECUTION_CONTEXT_NAMESPACE),
		@XmlNs(prefix = "xs", namespaceURI="http://www.w3.org/2001/XMLSchema"),
		@XmlNs(prefix = "xsi", namespaceURI="http://www.w3.org/2001/XMLSchema-instance")
	}
)

/**
 * Provides the adapted JAXB classes necessary to persist JobExecution data in MarkLogic
 *
 * @since 1.0

 */

package com.marklogic.spring.batch.core; 

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlNs;
