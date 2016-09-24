package com.marklogic.spring.batch.config;

import com.marklogic.client.spring.BasicConfig;
import com.marklogic.spring.batch.MainConfig;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.filter.AbstractClassTestingTypeFilter;

/**
 * Spring TypeFilter that is expected to be used to exclude certain Spring Configuration classes that are not
 * candidates for a job configuration.
 */
public class ConfigTypeFilter extends AbstractClassTestingTypeFilter {

	@Override
	protected boolean match(ClassMetadata metadata) {
		String name = metadata.getClassName();
		return name.startsWith("org.springframework")
			|| name.equals(MainConfig.class.getName())
			|| name.equals(BasicConfig.class.getName());
	}

}