package com.marklogic.example.geonames;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import org.geonames.Country;

public class CountryFieldSetMapper implements FieldSetMapper<Country> {

	@Override
	public Country mapFieldSet(FieldSet fieldSet) throws BindException {
		Country country = new Country();
		country.setIso(fieldSet.readString(0));
		country.setIso3(fieldSet.readString(1));
		country.setIsoNumeric(fieldSet.readString(2));
		country.setCountry(fieldSet.readString(3));
		country.setCapital(fieldSet.readString(4));
		country.setAreaSquareMeters(fieldSet.readInt(5));
		country.setPopulation(fieldSet.readInt(6));
		country.setContinent(fieldSet.readString(7));
		country.setTld(fieldSet.readString(8));
		
		
		return country;
	} 

}
