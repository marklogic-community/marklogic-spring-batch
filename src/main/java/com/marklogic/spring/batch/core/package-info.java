@XmlSchema(
	namespace=MarkLogicSpringBatch.NAMESPACE,
	elementFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,	
	xmlns = {
			@XmlNs(prefix = MarkLogicSpringBatch.NAMESPACE_PREFIX, namespaceURI=MarkLogicSpringBatch.NAMESPACE),
			@XmlNs(prefix = "inst", namespaceURI="http://marklogic.com/spring-batch/job-instance")
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
