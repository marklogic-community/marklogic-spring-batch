package com.marklogic.spring.batch.geonames;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import org.geonames.Geoname;

public class GeonameFieldSetMapper implements FieldSetMapper<Geoname>{

	@Override
	public Geoname mapFieldSet(FieldSet fieldSet) throws BindException {
		Geoname geo = new Geoname();
		geo.setId(fieldSet.readString(0));
		List<String> names = new ArrayList<>();
		
		//name
		names.add(fieldSet.readString(1));
		//ascii-name
		names.add(fieldSet.readString(2));
		//alternateNames
		List<String> altNames = Arrays.asList(fieldSet.readString(3).split(","));
		for (String altName : altNames) {
			names.add(altName);
		}	
		geo.setNames(names);
		geo.setLatitude(fieldSet.readFloat(4));
		geo.setLongitude(fieldSet.readFloat(5));
		geo.setFeatureClass(fieldSet.readString(6));
		geo.setFeatureCode(fieldSet.readString(7));
		geo.setCountryCode(fieldSet.readString(8));
		geo.setAltCountryCode(fieldSet.readString(9));
		geo.setAdmin1Code(fieldSet.readString(10));
		geo.setAdmin2Code(fieldSet.readString(11));
		geo.setAdmin3Code(fieldSet.readString(12));
		geo.setAdmin4Code(fieldSet.readString(13));
		geo.setPopulation(fieldSet.readString(14));
		geo.setElevation(fieldSet.readString(15));
		geo.setDigitalElevationModel(fieldSet.readString(16));
		geo.setTimeZone(fieldSet.readString(17));
		//geo.setModificationDate(fieldSet.read);
		
		return geo;
	}

}
