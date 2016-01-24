@XmlSchema(
	namespace="http://marklogic.com/spring-batch",
	elementFormDefault=javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
	
	xmlns = {
			@javax.xml.bind.annotation.XmlNs(prefix = "sb", namespaceURI="http://marklogic.com/spring-batch")
	}
)

package org.springframework.batch.core;

import javax.xml.bind.annotation.XmlSchema;