package com.marklogic.spring.batch.geonames.data;

import java.util.List;

public class Country {
	
	private String iso;
	private String iso3;
	private String isoNumeric;
	private String fips;
	private String country;
	private String capital;
	private int areaSquareMeters;
	private int population;
	private String continent;
	private String tld;
	private String currencyCode;
	private String currencyName;
	private String phone;
	private String postalCodeFormat;
	private String postalCodeRegex;
	private List<String> languages;
	private String geonameId;
	private List<String> neighbors;
	private String equivalentFipsCode;
	
	public String getIso() {
		return iso;
	}
	public void setIso(String iso) {
		this.iso = iso;
	}
	public String getIso3() {
		return iso3;
	}
	public void setIso3(String iso3) {
		this.iso3 = iso3;
	}
	public String getIsoNumeric() {
		return isoNumeric;
	}
	public void setIsoNumeric(String isoNumeric) {
		this.isoNumeric = isoNumeric;
	}
	public String getFips() {
		return fips;
	}
	public void setFips(String fips) {
		this.fips = fips;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getCapital() {
		return capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public int getAreaSquareMeters() {
		return areaSquareMeters;
	}
	public void setAreaSquareMeters(int areaSquareMeters) {
		this.areaSquareMeters = areaSquareMeters;
	}
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	public String getContinent() {
		return continent;
	}
	public void setContinent(String continent) {
		this.continent = continent;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getCurrencyName() {
		return currencyName;
	}
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPostalCodeFormat() {
		return postalCodeFormat;
	}
	public void setPostalCodeFormat(String postalCodeFormat) {
		this.postalCodeFormat = postalCodeFormat;
	}
	public String getPostalCodeRegex() {
		return postalCodeRegex;
	}
	public void setPostalCodeRegex(String postalCodeRegex) {
		this.postalCodeRegex = postalCodeRegex;
	}
	public List<String> getLanguages() {
		return languages;
	}
	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
	public String getGeonameId() {
		return geonameId;
	}
	public void setGeonameId(String geonameId) {
		this.geonameId = geonameId;
	}
	public List<String> getNeighbors() {
		return neighbors;
	}
	public void setNeighbors(List<String> neighbors) {
		this.neighbors = neighbors;
	}
	public String getEquivalentFipsCode() {
		return equivalentFipsCode;
	}
	public void setEquivalentFipsCode(String equivalentFipsCode) {
		this.equivalentFipsCode = equivalentFipsCode;
	}
	
	public String getTld() {
		return tld;
	}
	public void setTld(String tld) {
		this.tld = tld;
	}

}
