package com.marklogic.spring.batch.data;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class GeonameFieldSetMapper implements FieldSetMapper<Geoname>{

	@Override
	public Geoname mapFieldSet(FieldSet fieldSet) throws BindException {
		Geoname geo = new Geoname();
		geo.setId(fieldSet.readString(0));
		geo.setName(fieldSet.readString(1));
		geo.setAsciiName(fieldSet.readString(2));
		return geo;
	}

}
