package org.geonames;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="geoname", namespace="http://geonames.org")
public class Geoname {
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}	
	public float getLatitude() {
		return latitude;
	}
	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	public float getLongitude() {
		return longitude;
	}
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	public String getFeatureClass() {
		return featureClass;
	}
	public void setFeatureClass(String featureClass) {
		this.featureClass = featureClass;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getAltCountryCode() {
		return altCountryCode;
	}
	public void setAltCountryCode(String altCountryCode) {
		this.altCountryCode = altCountryCode;
	}
	public String getAdmin1Code() {
		return admin1Code;
	}
	public void setAdmin1Code(String admin1Code) {
		this.admin1Code = admin1Code;
	}
	public String getAdmin2Code() {
		return admin2Code;
	}
	public void setAdmin2Code(String admin2Code) {
		this.admin2Code = admin2Code;
	}
	public String getAdmin3Code() {
		return admin3Code;
	}
	public void setAdmin3Code(String admin3Code) {
		this.admin3Code = admin3Code;
	}
	public String getAdmin4Code() {
		return admin4Code;
	}
	public void setAdmin4Code(String admin4Code) {
		this.admin4Code = admin4Code;
	}
	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}
	public int getElevation() {
		return elevation;
	}
	public void setElevation(int elevation) {
		this.elevation = elevation;
	}
	public String getDigitalElevationModel() {
		return digitalElevationModel;
	}
	public void setDigitalElevationModel(String digitalElevationModel) {
		this.digitalElevationModel = digitalElevationModel;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	public Date getModificationDate() {
		return modificationDate;
	}
	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}
	
	public String getFeatureCode() {
		return featureCode;
	}
	public void setFeatureCode(String featureCode) {
		this.featureCode = featureCode;
	}
	
	@XmlTransient
	public List<String> getNames() {
		return name;
	}
	public void setNames(List<String> names) {
		this.name = names;
	}

	private String id;
	
	@XmlElementWrapper(name="names")
	@XmlElement
    private List<String> name;
	
	private float latitude;
    private float longitude;
    private String featureClass;
    private String featureCode;
    private String countryCode;
    private String altCountryCode;
    private String admin1Code;
    private String admin2Code;
    private String admin3Code;
    private String admin4Code;
    private int population;
    private int elevation;
    private String digitalElevationModel;
	private String timeZone;
    private Date modificationDate;
    
}

